package com.kiwi.webtest;

import com.google.gson.Gson;
import com.kiwi.webtest.cookies.CookieEntry;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TestCreation {
    private WebDriver driver;
    private Gson gson;

    public TestCreation(WebDriver driver) {
        this.driver = driver;
        this.gson = new Gson();

        this.driver.manage().window().maximize();
    }

    private void login() throws FileNotFoundException {
        driver.get("https://www.wjx.cn");
        CookieEntry[] cookieEntries = gson.fromJson(new FileReader("src/main/resources/cookies.json"), CookieEntry[].class);
        for (CookieEntry cookieEntry : cookieEntries) {
            driver.manage().addCookie(new Cookie(cookieEntry.getName(), cookieEntry.getValue()));
        }
        driver.get("https://www.wjx.cn/newwjx/manage/myquestionnaires.aspx");
    }

    private boolean createQuestionnaire() {
        System.out.println("[enter] 进入创建问卷窗口");
        if (isElementExistByXpath("//*[@id=\"ctl01_ContentPlaceHolder1_antiSpam_txtValInputCode\"]")) {
            System.out.println("[exit] 无法识别验证码");
            return false;
        }

        WebElement inputName = driver.findElement(By.id("ctl01_ContentPlaceHolder1_txtQName"));
        inputName.sendKeys("当代大学生脱发情况调查" + (new Random()).nextInt(1000));
        driver.findElement(By.id("ctl01_ContentPlaceHolder1_lbtnNextStep")).click();
        System.out.println("[click] 点击创建按钮");
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

    public void run() throws FileNotFoundException, InterruptedException {
        login();
        TimeUnit.SECONDS.sleep(1);
        clickByXpath("//*[@id=\"ctl01_ContentPlaceHolder1_searchPapaer\"]/div/a", "进入选择界面");
        clickByXpath("//*[@id=\"divModule\"]/div[1]/a", "选择“调查”类型");

        if (createQuestionnaire()) {
            editQuestionnaire();
            clickByXpath("//*[@id=\"hrefFiQ\"]", "完成编辑");
            clickByXpath("//*[@id=\"ctl02_ContentPlaceHolder1_btnRun\"]", "发布问卷");
            String questionnaireUrl = driver.findElement(By.xpath("//*[@id=\"ctl02_ContentPlaceHolder1_txtLink\"]")).getAttribute("value");
            System.out.println("[finish] 生成问卷： " + questionnaireUrl);
        }
    }

    private void clickByXpath(String xpath, String info) throws InterruptedException {
        System.out.println("[click] " + info);
        WebElement element = driver.findElement(By.xpath(xpath));
        element.click();
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
        TestCreation tc = new TestCreation(new ChromeDriver());
        tc.run();
    }
}
