package org.example.speedmonitoragent.batch;

import org.example.speedmonitoragent.entity.NetworkSpeedMetrics;
import org.example.speedmonitoragent.repository.NetworkSpeedMetricsRepository;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Konfigurationsklasse für die Spring Batch Pipeline.
 */
@Configuration
public class BatchConfig {

    @Bean
    public RepositoryItemWriter<NetworkSpeedMetrics> writer(NetworkSpeedMetricsRepository repository) {
        return new RepositoryItemWriterBuilder<NetworkSpeedMetrics>()
                .repository(repository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step speedTestStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              SeleniumSpeedTestReader reader,
                              RepositoryItemWriter<NetworkSpeedMetrics> writer) {
        return new StepBuilder("speedTestStep", jobRepository)
                .<NetworkSpeedMetrics, NetworkSpeedMetrics>chunk(1)
                .transactionManager(transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public Job speedTestJob(JobRepository jobRepository, Step speedTestStep) {
        return new JobBuilder("speedTestJob", jobRepository)
                .start(speedTestStep)
                .build();
    }
}