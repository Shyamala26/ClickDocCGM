package com.cgm.clickdoc.testsuite;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends Base {

	@Test(priority = 1)
	public void openClickDocURL() {

		String clickDocURL = "https://demo.clickdoc.de";
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get(clickDocURL);

		List<WebElement> cookiesElements = driver
				.findElements(By.xpath("//button[@type='button' and text()='Alle akzeptieren']"));
		if (!cookiesElements.isEmpty()) {
			cookiesElements.get(0).click();
		}

		Assert.assertTrue(driver.getCurrentUrl().equals("https://demo.clickdoc.de/cd-de/"));
		Assert.assertEquals(driver.getTitle(), "CLICKDOC - Arzttermine online buchen & Gesundheits-Apps");
	}

	@Test(priority = 2, dependsOnMethods = { "openClickDocURL" })
	public void checkLoginPageElementsBySelectingProfileButton() {

		findElement(By.xpath("//a[@angularticsaction='Open login iframe']/span[@class='bold-menu-text ng-tns-c117-0']"),
				10).click();

		Assert.assertTrue(isElementExist(By.cssSelector("div.login-iframe"), 10));
		Assert.assertTrue(isElementExist(By.cssSelector("span[class='iframe-dialog-close icon icon-CO_close']"), 10));

		driver.switchTo().frame("iframeDialog");

		Assert.assertTrue(isElementExist(By.xpath("//input[@type='email' and @placeholder='E-Mail-Adresse']"), 10));
		Assert.assertTrue(isElementExist(By.xpath("//input[@type='password' and @placeholder='Passwort']")));
		Assert.assertTrue(isElementExist(By.xpath("//a[@data-web-test='login_forgot_password']")));
		Assert.assertTrue(
				isElementExist(By.xpath("//button[@data-web-test='login_primary_btn' and text()='Anmelden']")));
		Assert.assertTrue(isElementExist(
				By.xpath("//button[@data-web-test='login_register_btn' and text()='CGM LIFE ID erstellen']")));
	}

	@Test(priority = 3, dependsOnMethods = { "checkLoginPageElementsBySelectingProfileButton" })
	public void checkLoginWithoutUsernameAndPassword() {

		driver.findElement(By.xpath("//button[@data-web-test='login_primary_btn' and text()='Anmelden']")).click();

		Assert.assertTrue(isElementExist(By.xpath("//mat-error[contains(text(), 'E-Mail-Adresse')]"), 5));
		Assert.assertTrue(isElementExist(By.xpath("//mat-error[contains(text(), 'Passwort')]"), 5));
	}

	@Test(priority = 4, dependsOnMethods = { "checkLoginWithoutUsernameAndPassword" })
	public void checkEmailInputFieldWithValidEmail() {

		driver.findElement(By.xpath("//input[@type='email' and @placeholder='E-Mail-Adresse']"))
				.sendKeys("dirk.nonn@cgm.com#1111");

		Assert.assertFalse(isElementExist(By.xpath("//mat-error[contains(text(), 'E-Mail-Adresse')]")));
	}

	@Test(priority = 5, dependsOnMethods = { "checkEmailInputFieldWithValidEmail" })
	public void checkPasswordInputFieldWithWrongPassword() {

		driver.findElement(By.xpath("//input[@type='password' and @placeholder='Passwort']")).sendKeys("abcdefg");

		Assert.assertFalse(isElementExist(By.xpath("//mat-error[contains(text(), 'Passwort')]")));
	}

	@Test(priority = 6, dependsOnMethods = { "checkPasswordInputFieldWithWrongPassword" })
	public void checkLoginWithWrongPassword() {

		checkLoginWithInvalidCredentials();
	}

	@Test(priority = 7, dependsOnMethods = { "checkPasswordInputFieldWithWrongPassword" })
	public void checkLoginWithInvalidEmail() {

		WebElement emailFieldElement = driver
				.findElement(By.xpath("//input[@type='email' and @placeholder='E-Mail-Adresse']"));
		emailFieldElement.clear();
		emailFieldElement.sendKeys("testmail.com");

		checkLoginWithInvalidCredentials();
	}

	@Test(priority = 8, dependsOnMethods = { "checkLoginWithInvalidEmail" })
	public void checkLoginWithValidUsernameAndPassword() {

		WebElement emailFieldElement = driver
				.findElement(By.xpath("//input[@type='email' and @placeholder='E-Mail-Adresse']"));
		emailFieldElement.clear();
		emailFieldElement.sendKeys("dirk.nonn@cgm.com#1111");

		WebElement passwordFieldElement = driver
				.findElement(By.xpath("//input[@type='password' and @placeholder='Passwort']"));
		passwordFieldElement.clear();
		passwordFieldElement.sendKeys("recruitingTest1!");

		driver.findElement(By.xpath("//button[@data-web-test='login_primary_btn' and text()='Anmelden']")).click();

		driver.switchTo().defaultContent();

		Assert.assertTrue(isElementExist(
				By.xpath("//li/a[@class='menu-link user-profile-dropdown-toggle ng-tns-c117-0']/app-avatar/div/img"),
				10));
		Assert.assertFalse(isElementExist(
				By.xpath("//a[@angularticsaction='Open login iframe']/span[@class='bold-menu-text ng-tns-c117-0']"),
				10));
	}

	@Test(priority = 9, dependsOnMethods = { "checkLoginWithValidUsernameAndPassword" })
	public void checkProfileButtonMenuOptions() {

		driver.findElement(
				By.xpath("//li/a[@class='menu-link user-profile-dropdown-toggle ng-tns-c117-0']/app-avatar/div/img"))
				.click();

		Assert.assertFalse(isElementExist(
				By.xpath("//a[@angularticsaction='Open login iframe']/span[@class='bold-menu-text ng-tns-c117-0']"),
				10));
		Assert.assertTrue(isElementExist(
				By.xpath("//li/div[@aria-labelledby='navbarDropdown']/div/a/div/span[text()=' Mein Profil ']")));
		Assert.assertTrue(isElementExist(
				By.xpath("//li/div[@aria-labelledby='navbarDropdown']/div/a/div/span[text()=' Logout ']")));
	}

	@Test(priority = 10, dependsOnMethods = { "checkProfileButtonMenuOptions" })
	public void checkLogout() {

		driver.findElement(By.xpath("//li/div[@aria-labelledby='navbarDropdown']/div/a/div/span[text()=' Logout ']"))
				.click();

		Assert.assertTrue(isElementExist(
				By.xpath("//a[@angularticsaction='Open login iframe']/span[@class='bold-menu-text ng-tns-c117-0']"),
				10));
	}

	private void checkLoginWithInvalidCredentials() {

		driver.findElement(By.xpath("//button[@data-web-test='login_primary_btn' and text()='Anmelden']")).click();

		Assert.assertTrue(isElementExist(By.xpath("//app-error-message[@data-web-test='login_failed']"), 10));

		WebElement findElement = driver.findElement(By.xpath("//div[@class='error-container']/div/p"));
		Assert.assertEquals(findElement.getText(),
				"Bitte überprüfen Sie Ihre Eingaben und probieren Sie es erneut. Haben Sie noch keine CGM LIFE ID?");

		Assert.assertTrue(isElementExist(By.xpath("//button[contains(text(), 'Jetzt registrieren')]")));
	}
}
