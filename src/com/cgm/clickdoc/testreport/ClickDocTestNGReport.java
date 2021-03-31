package com.cgm.clickdoc.testreport;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

public class ClickDocTestNGReport implements ITestListener {
	@Override
	public void onStart(ITestContext context) {
	}

	@Override
	public void onTestStart(ITestResult result) {
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		Reporter.log("Execution of Test-Step " + result.getName() + " Successful!");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		Reporter.log("Execution of Test-Step " + result.getName() + " Failed!");
		File screenShot = screenCapture(result.getTestContext());
		String htmlLink = "<b><a href=\"file://" + screenShot.getPath() + "\" target=\"_blank\"> "
				+ screenShot.getName() + "</a></b>";
		Reporter.log("Refer the attched screenshort: " + htmlLink);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		Reporter.log("Execution of Test-Step " + result.getName() + " Skipped!");
	}

	@Override
	public void onFinish(ITestContext context) {
		
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
	}

	private File screenCapture(ITestContext context) {
		String filePath = System.getProperty("user.dir") + "\\test-output\\screenshots\\ClickDoc" + System.currentTimeMillis() + ".png";
		TakesScreenshot scrShot = ((TakesScreenshot) context.getAttribute("WebDriver"));
		File srcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File destFile = new File(filePath);
		try {
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException e) {
			Reporter.log(e.getMessage());
		}
		return destFile;
	}
}
