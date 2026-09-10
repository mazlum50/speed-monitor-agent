package org.example.speedmonitoragent.batch;

import org.example.speedmonitoragent.entity.NetworkSpeedMetrics;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Custom ItemReader für Spring Batch, der Netzwerkmessdaten über Selenium erhebt.
 */
@Component
public class SeleniumSpeedTestReader implements ItemReader<NetworkSpeedMetrics> {

    private boolean isRead = false;

    @Override
    public NetworkSpeedMetrics read() {
        // Stellt sicher, dass der Reader pro Batch-Durchlauf nur einmal Daten liefert (beendet den Batch-Step)
        if (isRead) {
            isRead = false;
            return null;
        }

        // Konfiguration für den headless Chrome-Browser
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Anzeigeloser Modus für Hintergrundausführung
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        WebDriver driver = new ChromeDriver(options);

        try {
            // Aufruf der Speedtest-Webseite (Fast.com)
            driver.get("https://fast.com");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

            // Warten auf das Messergebnis für die Download-Geschwindigkeit
            WebElement speedValueElement = driver.findElement(By.id("speed-value"));

            // Kurze Wartezeit zur Erfassung des finalen Werts
            Thread.sleep(15000);

            Double downloadSpeed = Double.parseDouble(speedValueElement.getText());

            isRead = true;

            // Rückgabe des erfassten Messobjekts
            return NetworkSpeedMetrics.builder()
                    .timestamp(LocalDateTime.now())
                    .downloadSpeed(downloadSpeed)
                    .uploadSpeed(0.0) // Dummy-Wert oder Erweiterung für Upload
                    .ping(0.0)         // Dummy-Wert oder Erweiterung für Ping
                    .status("SUCCESS")
                    .build();

        } catch (Exception e) {
            isRead = true;
            return NetworkSpeedMetrics.builder()
                    .timestamp(LocalDateTime.now())
                    .status("FAILED")
                    .build();
        } finally {
            // Schließen des Browsers nach Abschluss der Messung
            driver.quit();
        }
    }
}
