
package edu.podportal.selenium;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotOnFailure
        implements TestExecutionExceptionHandler, AfterTestExecutionCallback {

    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(ScreenshotOnFailure.class);

    private static final String FAILURE_KEY = "testFailure";

    @Override
    public void handleTestExecutionException(
            ExtensionContext context, Throwable cause) throws Throwable {

        context.getStore(NAMESPACE).put(FAILURE_KEY, cause);
        throw cause;
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Throwable failure = context.getStore(NAMESPACE)
                .remove(FAILURE_KEY, Throwable.class);

        if (failure == null) {
            return;
        }

        Object instance = context.getRequiredTestInstance();

        if (!(instance instanceof BaseSeleniumTest)) {
            return;
        }

        WebDriver driver = ((BaseSeleniumTest) instance).driver;

        if (driver == null) {
            System.err.println(
                    "Screenshot skipped: WebDriver was not initialized.");
            return;
        }

        String className =
                context.getRequiredTestClass().getSimpleName();
        String methodName =
                context.getRequiredTestMethod().getName();

        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        Path screenshot = Path.of(
                "target", "selenium-screenshots",
                className + "-" + methodName + "-" + timestamp + ".png");

        try {
            Files.createDirectories(screenshot.getParent());

            byte[] image = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.BYTES);

            Files.write(screenshot, image);

            System.err.println("Failure screenshot saved: "
                    + screenshot.toAbsolutePath());

        } catch (Exception ex) {
            System.err.println("Could not save failure screenshot: "
                    + ex.getMessage());
        }
    }
}
