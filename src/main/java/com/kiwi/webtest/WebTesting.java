package com.kiwi.webtest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebTesting {

    public static void main(String[] args) {
        WebDriver chromeDriver = new ChromeDriver();
        String url = "http://www.baidu.com";
        chromeDriver.get(url);
    }
}
