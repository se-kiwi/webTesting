package com.kiwi.webtest;

import com.google.gson.Gson;
import com.kiwi.webtest.cookies.CookieEntry;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class TestManagement {
    private WebDriver driver;
    private Actions action;
    private Gson gson;

    public TestManagement(WebDriver driver) throws FileNotFoundException {
        this.driver = driver;
        this.action = new Actions(this.driver);
        this.gson = new Gson();

        this.driver.manage().window().maximize();
        driver.get("https://www.wjx.cn");
        setCookies();
    }

    private void setCookies() throws FileNotFoundException {
        CookieEntry[] cookieEntries = gson.fromJson(new FileReader("src/main/resources/cookies.json"), CookieEntry[].class);
        for (CookieEntry cookieEntry : cookieEntries) {
            driver.manage().addCookie(new Cookie(cookieEntry.getName(), cookieEntry.getValue()));
        }
    }

    private void saveCaptcha(BufferedImage fullImg, Point point, Dimension size, double scale) throws IOException {
        int x = (int) (point.getX() * scale);
        int y = (int) (point.getY() * scale);
        int w = (int) (size.getWidth() * scale);
        int h = (int) (size.getHeight() * scale);
        BufferedImage eleScreenshot= fullImg.getSubimage(x, y, w, h);
        ImageIO.write(eleScreenshot, "png", new File("src/main/resources/captcha_" + scale + ".png"));
    }

    private void getScreenshot() throws IOException {
        WebElement picElement = driver.findElement(By.xpath("//*[@id=\"spanCode\"]/span[2]/img"));

        File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        BufferedImage fullImg = ImageIO.read(screenshot);

        FileUtils.copyFile(screenshot, new File("src/main/resources/fullimg.png"));

        Point point = picElement.getLocation();
        Dimension size = picElement.getSize();
        int eleWidth = picElement.getSize().getWidth();
        int eleHeight = picElement.getSize().getHeight();

        saveCaptcha(fullImg, point, size, 1.0);
        saveCaptcha(fullImg, point, size, 1.25);
        saveCaptcha(fullImg, point, size, 1.5);
    }

    private boolean createQuestionnaire() throws InterruptedException {
        System.out.println("[enter] 进入创建问卷窗口");
        driver.findElement(By.id("ctl01_ContentPlaceHolder1_txtQName")).sendKeys("当代大学生脱发情况调查" + (new Random()).nextInt(1000));
        clickByXpath("//*[@id=\"ctl01_ContentPlaceHolder1_lbtnNextStep\"]", "点击创建按钮");

        int testCount = 0;
        while (isElementExistByXpath("//*[@id=\"ctl01_ContentPlaceHolder1_antiSpam_txtValInputCode\"]")) {
            testCount++;
            System.out.println("验证码" + testCount);
            if (testCount > 3) {
                System.out.println("[exit] 失败多次，正在退出");
                return false;
            }

            WebElement inputElement = driver.findElement(By.xpath("//*[@id=\"ctl01_ContentPlaceHolder1_antiSpam_txtValInputCode\"]"));
            inputElement.click();

            try {
                getScreenshot();
            } catch (IOException e) {
                System.out.println("[exit] 捕获验证码失败");
                return false;
            }
            Scanner scanner = new Scanner(System.in);
            String strCaptcha = scanner.nextLine();
            inputElement.click();
            inputElement.sendKeys(strCaptcha);

            clickByXpath("//*[@id=\"ctl01_ContentPlaceHolder1_lbtnNextStep\"]", "点击创建按钮");
        }

        return true;
    }

    private void editQuestionnaire() throws InterruptedException {
        clickByXpath("//*[@id=\"hrefChoice\"]", "添加单选框");
        clickByXpath("//*[@id=\"divNormal\"]/li[1]/ul/li[2]/a", "添加多选框");
        clickByXpath("//*[@id=\"divNormal\"]/li[2]/ul/li[2]/a", "添加多项填空");
        clickByXpath("//*[@id=\"divNormal\"]/li[5]/ul/li[3]/a", "添加评分单选");
        clickByXpath("//*[@id=\"divNormal\"]/li[4]/ul/li[4]/a", "添加矩阵滑动条");
        clickByXpath("//*[@id=\"divNormal\"]/li[5]/ul/li[2]/a", "添加NPS量表");
    }

    private void publishQuestionnaire() throws InterruptedException {
        System.out.println("*********** Publish ***********");
        driver.get("https://www.wjx.cn/newwjx/manage/myquestionnaires.aspx");
        TimeUnit.SECONDS.sleep(1);

        clickByXpath("//*[@id=\"ctl01_ContentPlaceHolder1_searchPapaer\"]/div/a", "进入选择界面");
        clickByXpath("//*[@id=\"divModule\"]/div[1]/a", "选择“调查”类型");

        if (createQuestionnaire()) {
            editQuestionnaire();
            clickByXpath("//*[@id=\"hrefFiQ\"]", "完成编辑");
            clickByXpath("//*[@id=\"ctl02_ContentPlaceHolder1_btnRun\"]", "发布问卷");

            if (isElementExistByXpath("//*[@id=\"layui-layer-iframe1\"]")) {
                System.out.println("[exit] 发布次数过多，需要绑定微信继续");
                return;
            }
            String questionnaireUrl = driver.findElement(By.xpath("//*[@id=\"ctl02_ContentPlaceHolder1_txtLink\"]")).getAttribute("value");
            System.out.println("[finish] 生成问卷： " + questionnaireUrl);
        }
    }

    private void showSearchResult(String words, String info) {
        System.out.println(info);
        if (isElementExistByXpath("//*[@id=\"ctl01_ContentPlaceHolder1_divEmptySearch\"]")) {
            String tip = driver.findElement(By.xpath("//*[@id=\"ctl01_ContentPlaceHolder1_lblNoQ\"]")).getText();
            System.out.println(tip);
            return;
        }

        WebElement searchResult = driver.findElement(By.xpath("//*[@id=\"ctl01_ContentPlaceHolder1_qls\"]"));
        List<WebElement> items = searchResult.findElements(By.className("survey-items"));
        List<String> titles = new ArrayList<String>();
        for (WebElement element : items) {
            WebElement titleElement = element.findElement(By.className("item-top")).findElement(By.className("pull-left")).findElement(By.tagName("a"));
            String title = titleElement.getAttribute("title");
            assert title.contains(words);
            titles.add(title);
        }
        System.out.println("搜索结果：" + titles.size());
        for (String title : titles) {
            System.out.println(title);
        }
    }

    private void searchQuestionnaire(String words) throws InterruptedException {
        System.out.println("*********** Search ***********");
        driver.get("https://www.wjx.cn/newwjx/manage/myquestionnaires.aspx");
        TimeUnit.SECONDS.sleep(1);

        WebElement searchInput = driver.findElement(By.xpath("//*[@id=\"ctl01_ContentPlaceHolder1_txtName\"]"));
        searchInput.sendKeys(words);
        clickByXpath("//*[@id=\"ctl01_ContentPlaceHolder1_btnSub\"]", "点击搜索按钮");
        showSearchResult(words, "在所有问卷中搜索：" + words);

        action.moveToElement(driver.findElement(By.xpath("//*[@id=\"ctl01_ContentPlaceHolder1_divStatus\"]/div/div/span"))).perform();
        TimeUnit.SECONDS.sleep(1);
        action.click(driver.findElement(By.xpath("//*[@id=\"ctl01_ContentPlaceHolder1_divStatus\"]/div/ul/li[3]"))).perform();
        showSearchResult(words, "在暂停状态的问卷中搜索：" + words);
    }

    private void modifyQuestionnaire() throws InterruptedException {
        System.out.println("*********** Modify ***********");
        driver.get("https://www.wjx.cn/newwjx/manage/myquestionnaires.aspx");
        TimeUnit.SECONDS.sleep(1);

        if (!isElementExistByXpath("//*[@id=\"ctl01_ContentPlaceHolder1_qls\"]/dl[1]/dd/div[1]/dl[1]/dd/a/i")) {
            System.out.println("[exit] 没有可编辑的问卷");
            return;
        }

        actionMoveByXpath("//*[@id=\"ctl01_ContentPlaceHolder1_qls\"]/dl[1]/dd/div[1]/dl[1]/dd/a", "选中第一个问卷");
        actionClickByXpath("//*[@id=\"ctl01_ContentPlaceHolder1_qls\"]/dl[1]/dd/div[1]/dl[1]/dd/ul/li[2]/a", "点击编辑按钮");
        actionClickByXpath("//*[@id=\"layui-layer1\"]/div[3]/a[1]", "确认编辑");

        actionClickByXpath("//*[@id=\"question\"]/div[2]", "编辑第一题");

        driver.switchTo().frame(driver.findElement(By.xpath("//*[@id=\"question\"]/div[2]/div[2]/div/div[2]/div[2]/table/tbody/tr[2]/td/table/tbody/tr/td/iframe")));
        WebElement textElement = driver.findElement(By.xpath("/html/body"));
        while (!textElement.getText().equals("")) {
            action.sendKeys(textElement, Keys.BACK_SPACE).perform();
        }
        action.sendKeys(textElement, "new title").perform();
        driver.switchTo().defaultContent();

        driver.findElement(By.xpath("//*[@id=\"question\"]/div[2]/div[2]/div/div[4]/div[2]/div/table/tbody/tr[2]/td[1]/input")).sendKeys("新的选项");
        WebElement allowEdit = driver.findElement(By.xpath("//*[@id=\"question\"]/div[2]/div[2]/div/div[4]/div[2]/div/table/tbody/tr[2]/td[4]/span/input"));
        if (!allowEdit.isSelected()) {
            allowEdit.click();
        }
        clickByXpath("//*[@id=\"question\"]/div[2]/div[2]/div/div[6]/input", "完成编辑");

        WebElement beginElement = driver.findElement(By.xpath("//*[@id=\"question\"]/div[2]/div/div[1]"));
        WebElement endElement = driver.findElement(By.xpath("//*[@id=\"question\"]/div[3]"));
        int dy = endElement.getRect().y - beginElement.getRect().y;
        action.clickAndHold(beginElement)
                .pause(1000)
                .moveByOffset(0, dy)
                .pause(1000)
                .release()
                .perform();
        TimeUnit.SECONDS.sleep(2);
        clickByXpath("//*[@id=\"hrefFiQ\"]", "保存编辑");
    }

    public void run() throws InterruptedException {
        publishQuestionnaire();
//        searchQuestionnaire("当代");
//        modifyQuestionnaire();
    }

    private void clickByXpath(String xpath, String info) throws InterruptedException {
        System.out.println("[click] " + info);
        WebElement element = driver.findElement(By.xpath(xpath));
        element.click();
        TimeUnit.SECONDS.sleep(1);
    }

    private void actionMoveByXpath(String xpath, String info) throws InterruptedException {
        System.out.println("[move] " + info);
        action.moveToElement(driver.findElement(By.xpath(xpath))).perform();
        TimeUnit.SECONDS.sleep(1);
    }

    private void actionClickByXpath(String xpath, String info) throws InterruptedException {
        System.out.println("[move] " + info);
        action.click(driver.findElement(By.xpath(xpath))).perform();
        TimeUnit.SECONDS.sleep(1);
    }

    private boolean isElementExistByXpath(String xpath) {
        try {
            driver.findElement(By.xpath(xpath));
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        TestManagement tc = new TestManagement(new ChromeDriver());
        tc.run();
    }
}
