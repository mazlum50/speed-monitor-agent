package org.example.speedmonitoragent.repository;

import org.example.speedmonitoragent.entity.NetworkSpeedMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository-Schnittstelle für den Datenbankzugriff auf Netzwerkmessdaten.
 */
@Repository
public interface NetworkSpeedMetricsRepository extends JpaRepository<NetworkSpeedMetrics, Long> {
}
