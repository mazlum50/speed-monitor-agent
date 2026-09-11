package org.example.speedmonitoragent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hauptklasse der Anwendung mit aktivierter Aufgabenplanung (Scheduling).
 */
@SpringBootApplication
@EnableScheduling
public class SpeedMonitorAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpeedMonitorAgentApplication.class, args);
	}

}
