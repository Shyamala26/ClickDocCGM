package com.cgm.clickdoc.testsuite;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;

import com.cgm.clickdoc.testreport.ClickDocTestNGReport;

@Listeners(ClickDocTestNGReport.class)
public abstract class Base {

	protected WebDriver driver;

	@BeforeTest
	public void setup(ITestContext testContext) {
		System.setProperty("webdriver.chrome.driver", "D:\\Selenium\\chromedriver_win32\\chromedriver.exe");
		driver = new ChromeDriver();
		testContext.setAttribute("WebDriver", driver);
		driver.manage().window().maximize();
	}

	@AfterTest
	public void teardown() {
		driver.quit();
	}

	protected WebElement findElement(By by, int timeoutInSeconds) {

		if (timeoutInSeconds > 0) {
			WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
			return wait.until(drv -> drv.findElement(by));
		}

		return driver.findElement(by);
	}

	protected boolean isElementExist(By by) {

		return isElementExist(by, 0);
	}

	protected boolean isElementExist(By by, int timeoutInSeconds) {

		if (timeoutInSeconds > 0) {
			WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
			return !wait.until(drv -> drv.findElements(by)).isEmpty();
		}

		return !driver.findElements(by).isEmpty();
	}

	protected boolean isElementExist(WebElement parentElement, By by) {

		return !parentElement.findElements(by).isEmpty();
	}

	protected void driverWait(int timeInSeconds) {
		try {
			Thread.sleep(TimeUnit.SECONDS.toMillis(timeInSeconds));
		} catch (InterruptedException e) {
			Reporter.log(e.getMessage());
		}
	}

}
