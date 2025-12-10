package com.ministerio.magia.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class JobCompletionListener implements JobExecutionListener {
    
    private static final Logger logger = LoggerFactory.getLogger(JobCompletionListener.class);
    
    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("=========================================");
        logger.info("Iniciando Job: {}", jobExecution.getJobInstance().getJobName());
        logger.info("Job ID: {}", jobExecution.getJobId());
        logger.info("Fecha de inicio: {}", LocalDateTime.now());
        logger.info("=========================================");
    }
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        LocalDateTime startTime = jobExecution.getStartTime() != null ? 
            jobExecution.getStartTime() : LocalDateTime.now();
        LocalDateTime endTime = jobExecution.getEndTime() != null ? 
            jobExecution.getEndTime() : LocalDateTime.now();
        
        Duration duration = Duration.between(startTime, endTime);
        
        logger.info("=========================================");
        logger.info("Finalizando Job: {}", jobExecution.getJobInstance().getJobName());
        logger.info("Job ID: {}", jobExecution.getJobId());
        logger.info("Estado: {}", jobExecution.getStatus());
        logger.info("Fecha de finalización: {}", endTime);
        logger.info("Duración: {} segundos", duration.getSeconds());
        logger.info("Exit Status: {}", jobExecution.getExitStatus());
        
        if (jobExecution.getStatus().isUnsuccessful()) {
            logger.error("Job finalizado con errores. Revisar logs para más detalles.");
        } else {
            logger.info("Job completado exitosamente");
        }
        
        logger.info("=========================================");
    }
}

