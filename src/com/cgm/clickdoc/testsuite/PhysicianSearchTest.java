package com.cgm.clickdoc.testsuite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PhysicianSearchTest extends Base {

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
	public void clickOnSearch() {
		driver.findElement(By.xpath("//span[@class='bolder-menu ng-tns-c117-0' and text()='Suchseite']")).click();

		Assert.assertTrue(isElementExist(By.cssSelector("div.filter")));
		Assert.assertTrue(isElementExist(By.cssSelector("div.sort")));
		Assert.assertTrue(isElementExist(
				By.cssSelector("div[class='card d-flex flex-column justify-content-center no-gutters']")));
	}

	@Test(priority = 3, dependsOnMethods = { "clickOnSearch" })
	public void checkSearchSection() {
		Assert.assertTrue(isElementExist(By.cssSelector("input#search-query-typeahead")));
		Assert.assertTrue(isElementExist(By.cssSelector("input#search-location-typeahead")));
		Assert.assertTrue(isElementExist(By.cssSelector("input#onlineBooking")));
		Assert.assertTrue(isElementExist(By.cssSelector("input#videoCall")));
		Assert.assertTrue(isElementExist(By.cssSelector("input#accessibility")));
		Assert.assertTrue(isElementExist(By.cssSelector("button[translate='doctorSearch.search.filter.submit']")));
	}

	@Test(priority = 4, dependsOnMethods = { "checkSearchSection" })
	public void checkSortingSection() {
		Assert.assertTrue(isElementExist(By.cssSelector("input#bestHit")));
		Assert.assertTrue(isElementExist(By.cssSelector("input#sortAlphabetically")));
		Assert.assertTrue(isElementExist(By.cssSelector("input#noLocation")));
		Assert.assertTrue(isElementExist(By.cssSelector("div.custom-slider")));
	}

	@Test(priority = 5, dependsOnMethods = { "checkSortingSection" })
	public void checkResultSection() {
		Assert.assertTrue(isElementExist(By.cssSelector("img[src='assets/img/no-results/file.svg']")));
		Assert.assertTrue(isElementExist(By.xpath(
				"//span[@translate='search.no.input.given.label' and text()='Auf der linken Seite kannst Du die Arztsuche starten.']")));
	}

	@Test(priority = 6, dependsOnMethods = { "checkResultSection" })
	public void checkNameField() {
		String input = "Beate";
		WebElement nameFieldElement = driver.findElement(By.cssSelector("input#search-query-typeahead"));
		nameFieldElement.sendKeys(input);
		checkNameInputFieldSearchList(input);
	}

	@Test(priority = 7, dependsOnMethods = { "checkNameField" })
	public void checkNameFieldFurtherInputRefineSearch() {
		String furtherInput = " Edel";
		WebElement nameFieldElement = driver.findElement(By.cssSelector("input#search-query-typeahead"));
		nameFieldElement.sendKeys(furtherInput);
		checkNameInputFieldSearchList("Beate Edel");
	}

	@Test(priority = 8, dependsOnMethods = { "checkNameFieldFurtherInputRefineSearch" })
	public void checkNameFieldFurtherInputWithNoSearchResults() {
		String furtherInput = "se";
		WebElement nameFieldElement = driver.findElement(By.cssSelector("input#search-query-typeahead"));
		nameFieldElement.sendKeys(furtherInput);

		WebDriverWait wait = new WebDriverWait(driver, 10);
		Boolean isElementInvisible = wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(
				"//typeahead-container[@class='dropdown open dropdown-menu']/button/div/span[text()='Name des Arztes']")));
		Assert.assertTrue(isElementInvisible);
	}

	@Test(priority = 9, dependsOnMethods = { "checkNameFieldFurtherInputWithNoSearchResults" })
	public void checkSearchResultSectionWithValidNameInput() {
		WebElement nameFieldElement = driver.findElement(By.cssSelector("input#search-query-typeahead"));
		nameFieldElement.clear();
		nameFieldElement.sendKeys("Beate" + Keys.ESCAPE);

		driver.findElement(By.xpath("//button[@translate='doctorSearch.search.filter.submit']")).click();

		WebElement searchResultsPanelElement = findElement(By.xpath("//div[@class='panel default-panel hide-filters']"),
				10);
		List<WebElement> contactTitleElements = searchResultsPanelElement
				.findElements(By.xpath("//h2[@class='contact-title']"));
		Assert.assertFalse(contactTitleElements.isEmpty());
		for (WebElement we : contactTitleElements) {
			Assert.assertTrue(we.getText().contains("Beate"));
		}
	}

	@Test(priority = 10, dependsOnMethods = { "checkSearchResultSectionWithValidNameInput" })
	public void checkSearchResultObjects() {

		List<WebElement> searchCardListElements = findSearchResultElements();

		Assert.assertFalse(searchCardListElements.isEmpty());

		for (WebElement we : searchCardListElements) {
			WebElement avatarElement = we.findElement(By.xpath(".//div[@class='avatar-container']/img"));
			String srcAttribute = avatarElement.getAttribute("src");
			Assert.assertNotNull(srcAttribute);
			Assert.assertFalse(srcAttribute.isEmpty());

			WebElement contactTitleElement = we.findElement(By.xpath(".//h2[@class='contact-title']"));
			String contactTitle = contactTitleElement.getText();
			Assert.assertFalse(contactTitle.isEmpty());

			Assert.assertTrue(isElementExist(
					By.xpath(".//app-profile-field/div/div/h4[@class='field-container__title' and text()='Adresse']")));

			List<WebElement> addressElements = we.findElements(By.xpath(
					".//app-profile-field/div/div/app-address-link-text/a/p[@class='address-profile-text ng-star-inserted']"));
			Assert.assertFalse(addressElements.isEmpty());
			for (WebElement addressWebElement : addressElements) {
				String address = addressWebElement.getText();
				Assert.assertNotNull(address);
				Assert.assertFalse(address.isEmpty());
			}

			List<WebElement> healthFacilityElements = we.findElements(By.xpath(
					".//app-profile-field/div/div/h4[@class='field-container__title' and text()='Name der Gesundheitseinrichtung']"));
			if (!healthFacilityElements.isEmpty()) {
				List<WebElement> practiceSpecializationElements = we.findElements(
						By.xpath(".//app-profile-field/div/div/a[@class='content-text--blue ng-star-inserted']"));
				Assert.assertFalse(practiceSpecializationElements.isEmpty());
				for (WebElement practiceSpecializationWebElement : practiceSpecializationElements) {
					String address = practiceSpecializationWebElement.getText();
					Assert.assertFalse(address.isEmpty());
				}
			}

			List<WebElement> onlineTermineSectionElements = we.findElements(By.xpath(".//app-contact-slots-details"));
			for (WebElement onlineTermineSectionElement : onlineTermineSectionElements) {
				List<WebElement> availableSlotsElements = onlineTermineSectionElement
						.findElements(By.xpath(".//div[@class='time-cell ng-star-inserted']"));
				for (WebElement availableSlotElement : availableSlotsElements) {
					Assert.assertFalse(availableSlotElement.findElement(By.xpath(".//span")).getText().isEmpty());
				}
				List<WebElement> unAvailableSlotsElements = onlineTermineSectionElement
						.findElements(By.xpath(".//div[@class='empty-time-cell ng-star-inserted']"));
				for (WebElement unAvailableSlotElement : unAvailableSlotsElements) {
					Assert.assertFalse(
							unAvailableSlotElement.findElements(By.xpath(".//div[@class='middle-line']")).isEmpty());
				}

				List<WebElement> terminBuchenElements = we.findElements(By.xpath(
						".//div[@class='booking-details-container medium-top-margin']/div/button[text()='Termin buchen']"));

				Assert.assertFalse(terminBuchenElements.isEmpty());
			}
		}
	}

	@Test(priority = 11, dependsOnMethods = { "checkSearchResultObjects" })
	public void scrollDownAndCheckShowMoreButtonAtPageBottom() {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
		Assert.assertTrue(isElementExist(By.xpath("//a[@class='load-more-link']"), 5));
	}

	@Test(priority = 12, dependsOnMethods = { "scrollDownAndCheckShowMoreButtonAtPageBottom" })
	public void checkAdditionalSearchResultsByClickingShowMoreButton() {

		List<WebElement> searchResultsBefore = findSearchResultElements();

		driver.findElement(By.xpath("//a[@class='load-more-link']")).click();

		driverWait(10);

		List<WebElement> searchResultsAfter = findSearchResultElements();

		Assert.assertTrue(searchResultsAfter.size() > searchResultsBefore.size());
	}

	@Test(priority = 13, dependsOnMethods = { "checkAdditionalSearchResultsByClickingShowMoreButton" })
	public void scrollUpAndCheckLocationInputField() {

		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
		driver.findElement(By.cssSelector("input#search-location-typeahead")).sendKeys("56567");
		WebElement locationFieldDropDownElement = findElement(
				By.xpath("//typeahead-container[@class='dropdown open dropdown-menu']"), 10);
		List<WebElement> locationFieldDropDownValuesElements = locationFieldDropDownElement
				.findElements(By.xpath("//button/span/strong"));
		Assert.assertFalse(locationFieldDropDownValuesElements.isEmpty());
		for (WebElement we : locationFieldDropDownValuesElements) {
			Assert.assertTrue(we.getText().contains("56567"));
		}
	}

	@Test(priority = 14, dependsOnMethods = { "scrollUpAndCheckLocationInputField" })
	public void checkSearchResultBySelectingLocation() {

		List<WebElement> searchResultsBefore = findSearchResultElements();

		WebElement locationFieldDropDownElement = findElement(
				By.xpath("//typeahead-container[@class='dropdown open dropdown-menu']"), 10);
		locationFieldDropDownElement
				.findElement(By.xpath("//button/span[text() = ' Neuwied']/strong[text() = '56567']")).click();

		driver.findElement(By.xpath("//button[@translate='doctorSearch.search.filter.submit']")).click();

		driverWait(10);

		List<WebElement> refinedSearchResults = findSearchResultElements();

		Assert.assertTrue(refinedSearchResults.size() <= searchResultsBefore.size());

		WebElement searchResultsPanelElement = driver
				.findElement(By.xpath("//div[@class='panel default-panel hide-filters']"));
		Assert.assertTrue(isElementExist(searchResultsPanelElement,
				By.xpath("//h2[@class='contact-title' and text()='Granate, Beate']")));
	}

	@Test(priority = 15, dependsOnMethods = { "checkSearchResultBySelectingLocation" })
	public void checkOnlineBookableCheckboxAndSubFields() {

		WebElement onlineBookingElement = driver
				.findElement(By.xpath("//input[@type='checkbox' and @id='onlineBooking']"));
		Assert.assertFalse(onlineBookingElement.isSelected());

		driver.findElement(By.xpath("//span[@translate='search.filter.checkbox.online.booking']")).click();

		Assert.assertTrue(isElementExist(By.xpath("//div[@class='day dropdown dropdown-select d-block']/button"), 10));
		Assert.assertTrue(isElementExist(By.xpath("//div[@class='time dropdown-select d-block dropdown']/button"), 10));
	}

	@Test(priority = 16, dependsOnMethods = { "checkOnlineBookableCheckboxAndSubFields" })
	public void clickSearchButtonAndCheckOnlineBookableAppointments() {

		driver.findElement(By.xpath("//button[@translate='doctorSearch.search.filter.submit']")).click();

		driverWait(10);

		WebElement searchResultsPanelElement = driver
				.findElement(By.xpath("//div[@class='panel default-panel hide-filters']"));

		Assert.assertTrue(isElementExist(searchResultsPanelElement,
				By.xpath("//h2[@class='contact-title' and text()='Granate, Beate']")));

		List<WebElement> onlineBookableSearchResultsElements = searchResultsPanelElement
				.findElements(By.xpath("//div[@class='card']"));
		Assert.assertFalse(onlineBookableSearchResultsElements.isEmpty());
		for (WebElement we : onlineBookableSearchResultsElements) {
			Assert.assertTrue(isElementExist(we, By.xpath(".//div[@class='contact-slots-details']")));
		}
	}

	@Test(priority = 17, dependsOnMethods = { "clickSearchButtonAndCheckOnlineBookableAppointments" })
	public void checkVideoConferenceSearchResults() {

		WebElement videoCallElement = driver.findElement(By.xpath("//input[@type='checkbox' and @id='videoCall']"));
		Assert.assertFalse(videoCallElement.isSelected());
		driver.findElement(By.xpath("//span[@translate='search.filter.checkbox.video.appointment']")).click();

		WebElement nameInputFieldElement = driver.findElement(By.xpath("//input[@id='search-query-typeahead']"));
		nameInputFieldElement.sendKeys(Keys.CONTROL + "a");
		nameInputFieldElement.sendKeys(Keys.DELETE);

		findElement(By.xpath("//button[@translate='doctorSearch.search.filter.submit']"), 10).click();

		driverWait(10);

		WebElement searchResultsPanelElement = driver
				.findElement(By.xpath("//div[@class='panel default-panel hide-filters']"));
		Assert.assertTrue(isElementExist(searchResultsPanelElement,
				By.xpath("//h2[@class='contact-title' and text()='Dr. Frida Fröhlich']")));
		List<WebElement> videoCallSearchResultsElements = searchResultsPanelElement
				.findElements(By.xpath("//div[@class='card']"));
		Assert.assertFalse(videoCallSearchResultsElements.isEmpty());

		for (WebElement we : videoCallSearchResultsElements) {
			Assert.assertTrue(isElementExist(we, By.xpath(
					".//app-profile-field/div/div/h4[@class='field-container__title' and text()='Angebotene Services']")));
			Assert.assertTrue(isElementExist(we,
					By.xpath(".//span[@class='field__element ng-star-inserted' and text()=' Videosprechstunde ']")));
		}
	}

	@Test(priority = 18, dependsOnMethods = { "checkVideoConferenceSearchResults" })
	public void checkBarrierFreeSearchResults() {

		WebElement videoCallElement = driver.findElement(By.xpath("//input[@type='checkbox' and @id='videoCall']"));
		Assert.assertTrue(videoCallElement.isSelected());
		driver.findElement(By.xpath("//span[@translate='search.filter.checkbox.video.appointment']")).click();

		WebElement accessibilityElement = driver
				.findElement(By.xpath("//input[@type='checkbox' and @id='accessibility']"));
		Assert.assertFalse(accessibilityElement.isSelected());
		driver.findElement(By.xpath("//span[@translate='search.filter.checkbox.accessibility']")).click();

		findElement(By.xpath("//button[@translate='doctorSearch.search.filter.submit']"), 10).click();

		driverWait(10);

		WebElement searchResultsPanelElement = driver
				.findElement(By.xpath("//div[@class='panel default-panel hide-filters']"));
		Assert.assertTrue(isElementExist(searchResultsPanelElement,
				By.xpath("//h2[@class='contact-title' and text()='Time, Dr. Peter']")));
		List<WebElement> accessibilitySearchResultsElements = searchResultsPanelElement
				.findElements(By.xpath("//div[@class='card']"));
		for (WebElement we : accessibilitySearchResultsElements) {
			List<WebElement> angeboteneServicesElements = we.findElements(By.xpath(
					".//app-profile-field/div/div/h4[@class='field-container__title' and text()='Angebotene Services']"));
			Assert.assertTrue(angeboteneServicesElements.size() >= 0);

			List<WebElement> videosprechstundeElements = we.findElements(
					By.xpath(".//span[@class='field__element ng-star-inserted' and text()=' Videosprechstunde ']"));
			Assert.assertTrue(videosprechstundeElements.size() >= 0);
		}
	}

	@Test(priority = 19, dependsOnMethods = { "checkBarrierFreeSearchResults" })
	public void checkAlphabeticalSortingOfSearchResults() {

		WebElement sortAlphabeticallyElement = driver
				.findElement(By.xpath("//input[@type='radio' and @id='sortAlphabetically']"));
		Assert.assertFalse(sortAlphabeticallyElement.isSelected());
		driver.findElement(By.xpath("//label[@for='sortAlphabetically']")).click();

		driverWait(10);

		List<String> names = new ArrayList<>();
		List<WebElement> searchResultsElements = findSearchResultElements();

		// Note: As Clarified with Mr. Giess, the sorting check is limited to
		// the first three results.
		for (int index = 0; index < 3 && index < searchResultsElements.size(); index++) {
			WebElement searchResultElement = searchResultsElements.get(index)
					.findElement(By.xpath(".//h2[@class='contact-title']"));
			names.add(searchResultElement.getText());
		}

		Assert.assertTrue(isSorted(names));
	}

	@Test(priority = 20, dependsOnMethods = { "checkAlphabeticalSortingOfSearchResults" })
	public void checkDistanceSort() {
		WebElement locationElement = findElement(By.xpath("//input[@type='radio' and @id='noLocation']"), 10);
		Assert.assertFalse(locationElement.isSelected());
		driver.findElement(By.xpath("//label[@for='noLocation']")).click();
	}

	@Test(priority = 21, dependsOnMethods = { "checkDistanceSort" })
	public void holdAndDragUmkreisSlider() {
		WebElement sliderElement = findElement(
				By.xpath("//span[@class='ng5-slider-span ng5-slider-pointer ng5-slider-pointer-min']"), 10);
		(new Actions(driver)).clickAndHold(sliderElement).sendKeys(Keys.ARROW_RIGHT).perform();
	}

	@Test(priority = 22, dependsOnMethods = { "holdAndDragUmkreisSlider" })
	public void releaseUmkreisSlider() {
		WebElement sliderElement = findElement(
				By.xpath("//span[@class='ng5-slider-span ng5-slider-pointer ng5-slider-pointer-min']"), 10);
		(new Actions(driver)).clickAndHold(sliderElement).release().perform();
	}

	
	private List<WebElement> findSearchResultElements() {
		
		WebElement searchResultsPanelElement = driver
				.findElement(By.xpath("//div[@class='panel default-panel hide-filters']"));
		return searchResultsPanelElement.findElements(By.xpath("//div[@class='card']"));
	}
	
	private void checkNameInputFieldSearchList(String searchInput) {
		
		WebElement dropdownListElement = findElement(
				By.xpath("//typeahead-container[@class='dropdown open dropdown-menu']"), 10);
		List<WebElement> dropdownListResultElements = dropdownListElement
				.findElements(By.xpath("//button/div/span/strong"));
		Assert.assertFalse(dropdownListResultElements.isEmpty());
		for (WebElement we : dropdownListResultElements) {
			Assert.assertEquals(we.getText(), searchInput);
		}
	}

	private boolean isSorted(List<String> listOfStrings) {
		if (listOfStrings.isEmpty() || listOfStrings.size() == 1) {
			return true;
		}

		Iterator<String> iter = listOfStrings.iterator();
		String current, previous = iter.next();
		while (iter.hasNext()) {
			current = iter.next();
			if (previous.compareTo(current) > 0) {
				return false;
			}
			previous = current;
		}
		return true;
	}
}
