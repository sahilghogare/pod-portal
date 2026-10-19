package edu.podportal.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

@ExtendWith(ScreenshotOnFailure.class)
public class DashboardJourneyTest extends BaseSeleniumTest {

    @Test
    void adminCanViewDeliverySummaryDashboard() {
        driver.get(baseUrl + "/login");

        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("Admin123!");
        driver.findElement(
                By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin/home"));

        driver.get(baseUrl + "/dashboard/summary");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.tagName("h1")));

        String pageText = driver.findElement(By.tagName("body")).getText();

        assertTrue(
                pageText.contains("Delivery Summary Dashboard"),
                "Dashboard heading should be visible");

        assertTrue(
                driver.findElement(By.cssSelector(".total")).isDisplayed(),
                "Total deliveries summary should be displayed");
    }
}