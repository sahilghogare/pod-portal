package edu.podportal.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

@ExtendWith(ScreenshotOnFailure.class)
public class CreateDeliveryJourneyTest extends BaseSeleniumTest {

    @Test
    void adminCanCreateDeliveryWithValidDetails() {
        driver.get(baseUrl + "/login");

        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("Admin123!");
        driver.findElement(
                By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin/home"));

        driver.get(baseUrl + "/deliveries/new");

        String trackingNumber = "SEL-" + System.currentTimeMillis();

        driver.findElement(By.id("trackingNo")).sendKeys(trackingNumber);
        driver.findElement(By.id("customer")).sendKeys("Selenium Test Customer");
        driver.findElement(By.id("address")).sendKeys("123 Test Street");
        driver.findElement(By.id("item")).sendKeys("Test Package");

        driver.findElement(
                By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/deliveries/"));

        assertTrue(
                driver.getCurrentUrl().matches(".*/deliveries/\\d+$"),
                "Successful delivery creation should open its detail page");
    }

    @Test
    void createDeliveryFormRequiresMandatoryFields() {
        driver.get(baseUrl + "/login");

        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("Admin123!");
        driver.findElement(
                By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin/home"));

        driver.get(baseUrl + "/deliveries/new");

        assertTrue(
                driver.findElement(By.id("trackingNo")).getAttribute("required") != null,
                "Tracking number should be required");
        assertTrue(
                driver.findElement(By.id("customer")).getAttribute("required") != null,
                "Customer should be required");
        assertTrue(
                driver.findElement(By.id("address")).getAttribute("required") != null,
                "Address should be required");
        assertTrue(
                driver.findElement(By.id("item")).getAttribute("required") != null,
                "Item should be required");

        driver.findElement(
                By.cssSelector("button[type='submit']")).click();

        assertTrue(
                driver.getCurrentUrl().endsWith("/deliveries/new"),
                "Submitting an empty form should not navigate away");
    }
}