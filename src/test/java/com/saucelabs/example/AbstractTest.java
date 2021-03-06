package com.saucelabs.example;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by grago on 27.09.17.
 */
public class AbstractTest {

    private WebDriver driver;
    protected Wikipedia wikipedia;

    @BeforeMethod
    @Parameters({ "browserName", "browserVersion", "os" })
    public void setup(String browserName, String browserVersion, String os, Method method) throws MalformedURLException {

        String testName = method.getName();

        // Config from env variables
        String username = System.getenv("SAUCE_USERNAME");
        String accessKey = System.getenv("SAUCE_ACCESS_KEY");
        String buildTag = System.getenv("BUILD_TAG");

        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability(CapabilityType.BROWSER_NAME, browserName);
        capabilities.setCapability(CapabilityType.VERSION, browserVersion);
        capabilities.setCapability(CapabilityType.PLATFORM, os);
        capabilities.setCapability("name", testName);

        if (buildTag != null) {
            capabilities.setCapability("build", buildTag);
        }

        driver = new RemoteWebDriver(new URL("https://" + username+ ":" + accessKey + "@ondemand.saucelabs.com/wd/hub"),
                capabilities);

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        wikipedia = new Wikipedia(driver);

        driver.get("https://wikipedia.org");

    }

    @AfterMethod
    public void teardown(ITestResult result) {
        ((JavascriptExecutor) driver).executeScript("sauce:job-result=" + (result.isSuccess() ? "passed" : "failed"));
        driver.quit();
        System.out.println("driver.quit() executed");
    }

}
