package com.xm.recommendation.configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

/**
 * Batch processing scheduler for periodically invoking import data job.
 */
@Configuration
@EnableScheduling
public class BatchProcessingSchedulerConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchProcessingSchedulerConfiguration.class);

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job importCryptoRecordsJob;

    /**
     * Main scheduled method for job invocation.
     * To be able to restart job 'uniqueness' parameter added to each job execution.
     */
    @Scheduled(fixedRateString = "${import.job.scheduler.fixed-rate:240000}")
    public void runImportJob() {
        final JobParameters jobParameters = new JobParametersBuilder()
                .addLong("uniqueness", System.nanoTime())
                .toJobParameters();

        try {
            final JobExecution execution = jobLauncher.run(importCryptoRecordsJob, jobParameters);

            final BatchStatus status = execution.getStatus();
            final LocalDateTime date= execution.getEndTime();
            final String jobName = execution.getJobInstance().getJobName();
            LOGGER.info("Import Job {} executed with status {} on {}", jobName, status.name(), date);
        } catch (JobExecutionException ex) {
            LOGGER.error("Error occurred during job {} execution. Error message: {}", importCryptoRecordsJob.getName(), ex.getMessage());
        }
    }
}
