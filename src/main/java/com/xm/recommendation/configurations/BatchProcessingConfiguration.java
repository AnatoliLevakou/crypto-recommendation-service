package com.xm.recommendation.configurations;

import com.xm.recommendation.listeners.JobCompletionNotificationListener;
import com.xm.recommendation.models.CryptoRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;

@Configuration
@EnableScheduling
public class BatchProcessingConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchProcessingConfiguration.class);
    private static final String IMPORT_JOB_NAME = "importCryptoRecordsJob";
    private static final String[] TOKENS = { "timestamp", "symbol", "price" };

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean("partitioner")
    @StepScope
    public Partitioner partitioner() {
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = null;

        try {
            resources = resolver.getResources("prices/*.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        partitioner.setResources(resources);
        partitioner.partition(10);
        return partitioner;
    }

    @Bean
    @StepScope
    @DependsOn("partitioner")
    public FlatFileItemReader<CryptoRecord> reader(@Value("#{stepExecutionContext['fileName']}") final String filename) throws MalformedURLException {
        return new FlatFileItemReaderBuilder<CryptoRecord>()
                .name("cryptoRecordsReader")
                .linesToSkip(1)
                .delimited()
                .names(TOKENS)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(CryptoRecord.class);
                }})
                .resource(new UrlResource(filename))
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<CryptoRecord> writer(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CryptoRecord>().itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO crypto (timestamp, symbol, price) VALUES (:timestamp, :symbol, :price)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importCryptoRecordsJob(FlatFileItemReader<CryptoRecord> reader, JdbcBatchItemWriter<CryptoRecord> writer, JobCompletionNotificationListener listener) {
        return new JobBuilder(IMPORT_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(importCryptoRecordsMasterStep(reader, writer))
                .end()
                .build();
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setQueueCapacity(10);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @Bean
    public Step importCryptoRecordsMasterStep(FlatFileItemReader<CryptoRecord> reader, JdbcBatchItemWriter<CryptoRecord> writer) {
        return new StepBuilder("importCryptoRecordsMasterStep", jobRepository)
                .partitioner("partitionerStep", partitioner())
                .step(importCryptoRecordsStep(reader, writer))
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step importCryptoRecordsStep(FlatFileItemReader<CryptoRecord> reader, JdbcBatchItemWriter<CryptoRecord> writer) {
        return new StepBuilder("importCryptoRecordsStep", jobRepository)
                .<CryptoRecord, CryptoRecord> chunk(10, transactionManager)
                .writer(writer)
                .reader(reader)
                .build();
    }
}
