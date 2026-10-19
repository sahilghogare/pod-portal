package edu.podportal.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

@ExtendWith(ScreenshotOnFailure.class)
public class StatusWorkflowJourneyTest extends BaseSeleniumTest {

    @Test
    void agentCanOpenAssignedDeliveriesWorkflow() {
        driver.get(baseUrl + "/login");

        driver.findElement(By.id("username")).sendKeys("agent");
        driver.findElement(By.id("password")).sendKeys("Agent123!");
        driver.findElement(
                By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/agent/home"));

        driver.get(baseUrl + "/agent/deliveries");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.tagName("h1")));

        String heading = driver.findElement(By.tagName("h1")).getText();

        assertTrue(
                heading.contains("My Assigned Deliveries"),
                "Agent should see the assigned deliveries workflow");
    }
}