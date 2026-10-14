package edu.podportal.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

@ExtendWith(ScreenshotOnFailure.class)
public class SearchJourneyTest extends BaseSeleniumTest {

    @Test
    void adminCanSearchDeliveriesByTrackingNumber() {
        driver.get(baseUrl + "/login");

        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("Admin123!");
        driver.findElement(
                By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin/home"));

        driver.get(baseUrl + "/deliveries");

        String trackingNumber = driver.findElement(
                By.cssSelector("table tbody tr td")).getText();

        driver.findElement(By.id("trackingNo")).sendKeys(trackingNumber);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/deliveries"));

        assertTrue(
                driver.findElement(By.tagName("body"))
                        .getText().contains(trackingNumber),
                "Search results should contain the requested tracking number");
    }
}