package com.xm.recommendation.configurations;

import com.xm.recommendation.batch.CryptoRecordMapper;
import com.xm.recommendation.batch.CustomMultiResourcePartitioner;
import com.xm.recommendation.models.CryptoRecord;
import com.xm.recommendation.properties.ImportJobProperties;
import com.xm.recommendation.repositories.ConfigurationRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
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

/**
 * Batch import configuration.
 * Implementation for partitioning batch processing.
 * Custom multi file partitioner @CustomMultiResourcePartitioner implemented to achieve ability for
 * filtering restricted files.
 */
@Configuration
@EnableScheduling
public class BatchProcessingConfiguration {
    private static final String IMPORT_JOB_NAME = "importCryptoRecordsJob";
    private static final String IMPORT_JOB_READER_NAME = "cryptoRecordsReader";
    private static final String IMPORT_JOB_MASTER_STEP_NAME = "importCryptoRecordsMasterStep";
    private static final String IMPORT_JOB_PARTITIONER_STEP_NAME = "partitionerStep";
    private static final String IMPORT_JOB_STEP_NAME = "importCryptoRecordsStep";
    private static final String IMPORT_JOB_WRITER_INSERT_SQL = "INSERT INTO crypto (timestamp, symbol, price) VALUES (:timestamp, :symbol, :price)";
    private static final String[] TOKENS = { "timestamp", "symbol", "price" };

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private Partitioner partitioner;
    @Autowired
    private ConfigurationRepository configurationRepository;
    @Autowired
    private ImportJobProperties importJobProperties;

    @Bean
    public Job importCryptoRecordsJob(FlatFileItemReader<CryptoRecord> reader,
                                      JdbcBatchItemWriter<CryptoRecord> writer) {
        return new JobBuilder(IMPORT_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(importCryptoRecordsMasterStep(reader, writer))
                .end()
                .build();
    }

    @Bean("partitioner")
    @StepScope
    public Partitioner partitioner() {
        final CustomMultiResourcePartitioner partitioner = new CustomMultiResourcePartitioner(configurationRepository);
        final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        Resource[] resources = null;

        try {
            resources = resolver.getResources(this.importJobProperties.getFilePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        partitioner.setResources(resources);
        partitioner.partition(1);
        return partitioner;
    }

    @Bean
    @StepScope
    @DependsOn("partitioner")
    public FlatFileItemReader<CryptoRecord> reader(@Value("#{stepExecutionContext['fileName']}") final String filename)
            throws MalformedURLException {
        LineMapper<CryptoRecord> lineMapper = importingLineMapper(null);
        return new FlatFileItemReaderBuilder<CryptoRecord>()
                .name(IMPORT_JOB_READER_NAME)
                .linesToSkip(1)
                .delimited()
                .names(TOKENS)
                .lineMapper(lineMapper)
                .resource(new UrlResource(filename))
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<CryptoRecord> writer(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CryptoRecord>().itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql(IMPORT_JOB_WRITER_INSERT_SQL)
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Step importCryptoRecordsMasterStep(final FlatFileItemReader<CryptoRecord> reader,
                                              final JdbcBatchItemWriter<CryptoRecord> writer) {
        return new StepBuilder(IMPORT_JOB_MASTER_STEP_NAME, jobRepository)
                .partitioner(IMPORT_JOB_PARTITIONER_STEP_NAME, partitioner)
                .step(importCryptoRecordsStep(reader, writer))
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step importCryptoRecordsStep(final FlatFileItemReader<CryptoRecord> reader,
                                        final JdbcBatchItemWriter<CryptoRecord> writer) {
        return new StepBuilder(IMPORT_JOB_STEP_NAME, jobRepository)
                .<CryptoRecord, CryptoRecord> chunk(this.importJobProperties.getChunkSize(), transactionManager)
                .writer(writer)
                .reader(reader)
                .build();
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(this.importJobProperties.getMaxPoolSize());
        taskExecutor.setCorePoolSize(this.importJobProperties.getCorePoolSize());
        taskExecutor.setQueueCapacity(this.importJobProperties.getQueueCapacity());
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @Bean
    public LineMapper<CryptoRecord> importingLineMapper(CryptoRecordMapper mapper) {
        DefaultLineMapper<CryptoRecord> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
        lineMapper.setFieldSetMapper(mapper);

        return lineMapper;
    }
}
