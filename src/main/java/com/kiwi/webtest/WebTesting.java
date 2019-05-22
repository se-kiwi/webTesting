package com.kiwi.webtest;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class WebTesting {
    static private  WebDriver driver ;
    private static String url = "https://www.wjx.cn/jq/14514689.aspx";
    @BeforeClass
    public static void init() {
        driver = WebDriverFactory.createDriver(WebDriverFactory.TYPE_CH);

    }
    @AfterClass
    public  static  void  close(){
        driver.close();
    }

    @Before
    public void switchTo() throws InterruptedException {
        driver.get(url);
        driver.switchTo().defaultContent();
        sleep(10);

    }
    @Test
    public void testRadio() {
        WebElement raidio1= driver.findElement(By.xpath("//*[@id=\"divquestion1\"]/ul/li[1]/a"));
        WebElement raidioInput1 = driver.findElement(By.xpath("//*[@id=\"q1_1\"]"));
        WebElement raidio2= driver.findElement(By.xpath("//*[@id=\"divquestion1\"]/ul/li[2]/a"));
        WebElement raidioInput2 = driver.findElement(By.xpath("//*[@id=\"q1_2\"]"));


        raidio1.click();
         assertTrue(raidioInput1.isSelected());
        assertFalse(raidioInput2.isSelected());

        raidio2.click();
        assertTrue(raidioInput2.isSelected());
        assertFalse(raidioInput1.isSelected());

//        raidioInput2.clear();
//        assertFalse(raidioInput2.isSelected());
//        assertFalse(raidioInput1.isSelected());
    }

    @Test
    public void testCheckBox(){
        WebElement checkBox1 = driver.findElement(By.xpath("//*[@id=\"divquestion2\"]/ul/li[1]/a"));
        WebElement checkBoxInput1 = driver.findElement(By.xpath("//*[@id=\"q2_1\"]"));
        WebElement checkBox2 = driver.findElement(By.xpath("//*[@id=\"divquestion2\"]/ul/li[2]/a"));
        WebElement checkBoxInput2 = driver.findElement(By.xpath("//*[@id=\"q2_2\"]"));

        checkBox1.click();
        assertTrue(checkBoxInput1.isSelected());

        checkBox2.click();
        assertTrue(checkBoxInput1.isSelected());
        assertTrue(checkBoxInput2.isSelected());

        checkBox1.click();
        assertFalse(checkBoxInput1.isSelected());
        assertTrue(checkBoxInput2.isSelected());
    }

    @Test
    public void  testInput(){
        WebElement input = driver.findElement(By.id("q6"));

        input.sendKeys("13878789696");
        assertEquals(input.getAttribute("value"),"13878789696");

        input.clear();
        assertEquals(input.getAttribute("value"),"");
    }



    @Test
    public void testSelect(){
        Select select = new Select(driver.findElement(By.id("q7")));

        select.selectByIndex(2);
        assertEquals(select.getFirstSelectedOption().getText(),"快速消费品(食品/饮料/化妆品)");

        select.selectByValue("3");
        assertEquals(select.getFirstSelectedOption().getText(),"批发/零售");

        select.selectByVisibleText("家具/工艺品/玩具");
        assertEquals(select.getFirstSelectedOption().getText(),"家具/工艺品/玩具");
    }

    @Test
    public void testUploadFile(){
        driver.switchTo().frame("uploadFrame3");
        WebElement fileUpload = driver.findElement(By.xpath("//*[@id=\"fileUpload\"]"));
        //must be absolute address
        File file = new File("src/main/resources/SortCompare.png");
        fileUpload.sendKeys(file.getAbsolutePath());

        driver.switchTo().defaultContent();
        WebElement uploadMsg = driver.findElement(By.className("uploadmsg"));
        assertEquals(uploadMsg.getText(),"文件已经成功上传！");
    }

    private WebElement getCell(WebElement Row, int cell,boolean th) {
        List<WebElement> cells;
        WebElement target = null;
        // 列里面有"<th>"、"<td>"两种标签，所以分开处理。
        if (Row.findElements(By.tagName("th")).size() >= cell+1 && th) {
            cells = Row.findElements(By.tagName("th"));
            target = cells.get(cell);
        }
        if (Row.findElements(By.tagName("td")).size() >= cell+1 && !th) {
            cells = Row.findElements(By.tagName("td"));
            target = cells.get(cell);
        }
        return target;
    }


    @Test
    public void testTable(){
        WebElement table = driver.findElement(By.xpath("//*[@id=\"divquestion5\"]/table"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        WebElement head = rows.get(0);
        WebElement row1 = rows.get(1);
        WebElement row2 = rows.get(2);

        assertEquals(getCell(head,0,false).getText(),"整洁");
        assertEquals(getCell(head,1,false).getText(),"美味");
        assertEquals(getCell(head,2,false).getText(),"综合");
        assertEquals(getCell(row1,0,true).getText(),"一餐");
        assertEquals(getCell(row2,0,true).getText(),"二餐");
    }

    @Test
    public void testButtonPageSwitch(){
        WebElement bt = driver.findElement(By.xpath("//*[@id=\"btnNext\"]"));
        bt.click();
        assertFalse(bt.isDisplayed());
    }

    @Test
    public void testCommitPageSwitch() throws InterruptedException {
        WebElement np_bt = driver.findElement(By.xpath("//*[@id=\"btnNext\"]"));
        WebElement commit_bt = driver.findElement(By.xpath("//*[@id=\"submit_button\"]"));
        np_bt.click();
        commit_bt.click();
        sleep(2000);
        try {
            WebElement title = driver.findElement(By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_JQ1_h1Name\"]"));
            fail();
        }catch (NoSuchElementException e){
            assertFalse(false);
        }

    }


}
