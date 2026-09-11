package org.example.speedmonitoragent.batch;


import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler-Klasse zur automatischen und periodischen Ausführung des Speedtest-Batch-Jobs.
 */
@Component
public class SpeedTestScheduler {

    private final JobOperator jobOperator;
    private final Job speedTestJob;

    public SpeedTestScheduler(JobOperator jobOperator, Job speedTestJob) {
        this.jobOperator = jobOperator;
        this.speedTestJob = speedTestJob;
    }

    /**
     * Führt den Batch-Job periodisch aus.
     * fixedRate: Intervall in Millisekunden (3600000 ms = 1 Stunde)
     * initialDelay: Wartezeit nach Start (10000 ms = 10 Sekunden)
     */
    @Scheduled(fixedRate = 3600000, initialDelay = 10000)
    public void runSpeedTestJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            // Ausführung über die moderne JobOperator API von Spring Batch 6
            jobOperator.start(speedTestJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}