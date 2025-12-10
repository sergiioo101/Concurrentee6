package com.ministerio.magia.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class CustomStepExecutionListener implements StepExecutionListener {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomStepExecutionListener.class);
    
    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.info("Iniciando Step: {}", stepExecution.getStepName());
        logger.info("Step ID: {}", stepExecution.getId());
    }
    
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        LocalDateTime startTime = stepExecution.getStartTime() != null ? 
            stepExecution.getStartTime() : LocalDateTime.now();
        LocalDateTime endTime = stepExecution.getEndTime() != null ? 
            stepExecution.getEndTime() : LocalDateTime.now();
        
        Duration duration = Duration.between(startTime, endTime);
        
        logger.info("Finalizando Step: {}", stepExecution.getStepName());
        logger.info("Estado: {}", stepExecution.getStatus());
        logger.info("Items leídos: {}", stepExecution.getReadCount());
        logger.info("Items escritos: {}", stepExecution.getWriteCount());
        logger.info("Items procesados: {}", stepExecution.getProcessSkipCount());
        logger.info("Items omitidos: {}", stepExecution.getSkipCount());
        logger.info("Items con error: {}", stepExecution.getFailureExceptions().size());
        logger.info("Duración: {} segundos", duration.getSeconds());
        
        if (stepExecution.getStatus().isUnsuccessful()) {
            logger.error("Step finalizado con errores");
            stepExecution.getFailureExceptions().forEach(exception -> 
                logger.error("Error en step: {}", exception.getMessage(), exception)
            );
        }
        
        return stepExecution.getExitStatus();
    }
}

