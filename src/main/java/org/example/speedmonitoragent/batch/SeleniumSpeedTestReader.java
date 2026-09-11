package org.example.speedmonitoragent.batch;

import org.example.speedmonitoragent.entity.NetworkSpeedMetrics;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Reader-Klasse zur Erfassung von Download-, Upload- und Ping-Messwerten via Selenium.
 */
@Component
public class SeleniumSpeedTestReader implements ItemReader<NetworkSpeedMetrics> {

    private boolean isRead = false;

    @Override
    public NetworkSpeedMetrics read() {
        if (isRead) {
            isRead = false; // Zurücksetzen für den nächsten Durchlauf
            return null;
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

        try {
            driver.get("https://fast.com");

            // 1. Warten bis der Download-Messvorgang abgeschlossen ist (Klasse 'succeeded' am Element)
            wait.until(ExpectedConditions.attributeContains(By.id("speed-value"), "class", "succeeded"));

            WebElement speedValueElement = driver.findElement(By.id("speed-value"));
            double downloadSpeed = parseDoubleSafely(speedValueElement.getText());

            // 2. Klick auf "Mehr Informationen anzeigen"
            try {
                WebElement showMoreBtn = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id("show-more-details-link"))
                );
                showMoreBtn.click();

                // 3. Warten bis Upload-Messung abgeschlossen ist
                wait.until(ExpectedConditions.attributeContains(By.id("upload-metric"), "class", "succeeded"));
            } catch (Exception ignored) {
                // Falls Zusatzinformationen fehlschlagen, behalten wir den Download-Wert
            }

            // 4. Werte auslesen
            double uploadSpeed = 0.0;
            try {
                WebElement uploadElement = driver.findElement(By.id("upload-value"));
                uploadSpeed = parseDoubleSafely(uploadElement.getText());
            } catch (Exception ignored) {}

            double ping = 0.0;
            try {
                WebElement pingElement = driver.findElement(By.id("latency-value"));
                ping = parseDoubleSafely(pingElement.getText());
            } catch (Exception ignored) {}

            isRead = true;

            return NetworkSpeedMetrics.builder()
                    .timestamp(LocalDateTime.now())
                    .downloadSpeed(downloadSpeed)
                    .uploadSpeed(uploadSpeed)
                    .ping(ping)
                    .status("SUCCESS")
                    .build();

        } catch (Exception e) {
            isRead = true;
            return NetworkSpeedMetrics.builder()
                    .timestamp(LocalDateTime.now())
                    .downloadSpeed(0.0)
                    .uploadSpeed(0.0)
                    .ping(0.0)
                    .status("FAILED")
                    .build();
        } finally {
            driver.quit();
        }
    }

    private double parseDoubleSafely(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}