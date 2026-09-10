package org.example.speedmonitoragent.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entitätsklasse zur Repräsentation der Netzwerkmessdaten in der Datenbank.
 */
@Entity
@Table(name = "network_speed_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkSpeedMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Zeitstempel der Durchführung der Messung
    private LocalDateTime timestamp;

    // Gemessene Download-Geschwindigkeit in Mbit/s
    private Double downloadSpeed;

    // Gemessene Upload-Geschwindigkeit in Mbit/s
    private Double uploadSpeed;

    // Gemessene Latenzzeit (Ping) in Millisekunden
    private Double ping;

    // Status des Messvorgangs (z. B. "SUCCESS" oder "FAILED")
    private String status;
}