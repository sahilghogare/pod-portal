package edu.podportal.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

@ExtendWith(ScreenshotOnFailure.class)
public class LoginJourneyTest extends BaseSeleniumTest {

    @Test
    void validAdminLoginOpensAdminHome() {
        driver.get(baseUrl + "/login");

        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("Admin123!");
        driver.findElement(
                By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/admin/home"));

        assertTrue(
                driver.getCurrentUrl().contains("/admin/home"),
                "Valid admin login should open the admin home page");
    }

    @Test
    void invalidLoginShowsErrorMessage() {
        driver.get(baseUrl + "/login");

        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("WrongPassword123!");
        driver.findElement(
                By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert.error")));

        String errorMessage = driver.findElement(
                By.cssSelector(".alert.error")).getText();

        assertTrue(
                errorMessage.contains("Invalid username or password"),
                "Invalid login should display an error message");
    }

}