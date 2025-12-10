package com.ministerio.magia.config;

import com.ministerio.magia.batch.listener.CustomChunkListener;
import com.ministerio.magia.batch.listener.JobCompletionListener;
import com.ministerio.magia.batch.listener.CustomStepExecutionListener;
import com.ministerio.magia.batch.processor.ArtefactoItemProcessor;
import com.ministerio.magia.batch.processor.HechizoItemProcessor;
import com.ministerio.magia.batch.processor.RegistroMagicoItemProcessor;
import com.ministerio.magia.batch.reader.ArtefactoItemReader;
import com.ministerio.magia.batch.reader.HechizoItemReader;
import com.ministerio.magia.batch.reader.RegistroMagicoItemReader;
import com.ministerio.magia.batch.writer.ArtefactoItemWriter;
import com.ministerio.magia.batch.writer.HechizoItemWriter;
import com.ministerio.magia.batch.writer.RegistroMagicoItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private PlatformTransactionManager transactionManager;
    
    @Autowired
    private JobCompletionListener jobCompletionListener;
    
    @Autowired
    private CustomStepExecutionListener stepExecutionListener;
    
    @Autowired
    private CustomChunkListener chunkListener;
    
    // Readers
    @Autowired
    private HechizoItemReader hechizoItemReader;
    
    @Autowired
    private ArtefactoItemReader artefactoItemReader;
    
    @Autowired
    private RegistroMagicoItemReader registroMagicoItemReader;
    
    // Processors
    @Autowired
    private HechizoItemProcessor hechizoItemProcessor;
    
    @Autowired
    private ArtefactoItemProcessor artefactoItemProcessor;
    
    @Autowired
    private RegistroMagicoItemProcessor registroMagicoItemProcessor;
    
    // Writers
    @Autowired
    private HechizoItemWriter hechizoItemWriter;
    
    @Autowired
    private ArtefactoItemWriter artefactoItemWriter;
    
    @Autowired
    private RegistroMagicoItemWriter registroMagicoItemWriter;
    
    // Step para procesar hechizos
    @Bean
    public Step procesarHechizosStep() {
        return new StepBuilder("procesarHechizosStep", jobRepository)
                .<com.ministerio.magia.model.Hechizo, com.ministerio.magia.model.Hechizo>chunk(10, transactionManager)
                .reader(hechizoItemReader)
                .processor(hechizoItemProcessor)
                .writer(hechizoItemWriter)
                .listener(stepExecutionListener)
                .listener(chunkListener)
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .skip(Exception.class)
                .skipLimit(100)
                .build();
    }
    
    // Step para procesar artefactos
    @Bean
    public Step procesarArtefactosStep() {
        return new StepBuilder("procesarArtefactosStep", jobRepository)
                .<com.ministerio.magia.model.Artefacto, com.ministerio.magia.model.Artefacto>chunk(10, transactionManager)
                .reader(artefactoItemReader)
                .processor(artefactoItemProcessor)
                .writer(artefactoItemWriter)
                .listener(stepExecutionListener)
                .listener(chunkListener)
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .skip(Exception.class)
                .skipLimit(100)
                .build();
    }
    
    // Step para procesar registros mágicos
    @Bean
    public Step procesarRegistrosMagicosStep() {
        return new StepBuilder("procesarRegistrosMagicosStep", jobRepository)
                .<com.ministerio.magia.model.RegistroMagico, com.ministerio.magia.model.RegistroMagico>chunk(10, transactionManager)
                .reader(registroMagicoItemReader)
                .processor(registroMagicoItemProcessor)
                .writer(registroMagicoItemWriter)
                .listener(stepExecutionListener)
                .listener(chunkListener)
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .skip(Exception.class)
                .skipLimit(100)
                .build();
    }
    
    // Job principal que procesa todos los datos mágicos
    @Bean
    public Job procesarDatosMagicosJob() {
        return new JobBuilder("procesarDatosMagicosJob", jobRepository)
                .start(procesarHechizosStep())
                .next(procesarArtefactosStep())
                .next(procesarRegistrosMagicosStep())
                .listener(jobCompletionListener)
                .build();
    }
    
    // Job individual para procesar solo hechizos
    @Bean
    public Job procesarHechizosJob() {
        return new JobBuilder("procesarHechizosJob", jobRepository)
                .start(procesarHechizosStep())
                .listener(jobCompletionListener)
                .build();
    }
    
    // Job individual para procesar solo artefactos
    @Bean
    public Job procesarArtefactosJob() {
        return new JobBuilder("procesarArtefactosJob", jobRepository)
                .start(procesarArtefactosStep())
                .listener(jobCompletionListener)
                .build();
    }
    
    // Job individual para procesar solo registros mágicos
    @Bean
    public Job procesarRegistrosMagicosJob() {
        return new JobBuilder("procesarRegistrosMagicosJob", jobRepository)
                .start(procesarRegistrosMagicosStep())
                .listener(jobCompletionListener)
                .build();
    }
}

