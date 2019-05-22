package com.kiwi.webtest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

class WebDriverFactory {
    public static final int TYPE_ED = 1;
    public static final int TYPE_FOX = 2;
    public static final int TYPE_CH = 3;

    static WebDriver createDriver(int type) {
        switch (type) {
            case TYPE_ED:
                return new EdgeDriver();
            case TYPE_FOX:
                return new FirefoxDriver();
            case TYPE_CH:
            default:
                return new ChromeDriver();
        }
    }
}
