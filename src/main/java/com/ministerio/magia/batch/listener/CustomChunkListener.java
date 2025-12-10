package com.ministerio.magia.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Component
public class CustomChunkListener implements ChunkListener {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomChunkListener.class);
    
    @Override
    public void beforeChunk(ChunkContext context) {
        logger.debug("Iniciando chunk - Step: {}", context.getStepContext().getStepName());
    }
    
    @Override
    public void afterChunk(ChunkContext context) {
        long readCount = context.getStepContext().getStepExecution().getReadCount();
        long writeCount = context.getStepContext().getStepExecution().getWriteCount();
        logger.debug("Chunk completado - Items leídos: {}, Items escritos: {}", readCount, writeCount);
    }
    
    @Override
    public void afterChunkError(ChunkContext context) {
        logger.error("Error en chunk - Step: {}", context.getStepContext().getStepName());
        if (context.getStepContext().getStepExecution().getFailureExceptions() != null) {
            context.getStepContext().getStepExecution().getFailureExceptions().forEach(exception ->
                logger.error("Excepción en chunk: {}", exception.getMessage(), exception)
            );
        }
    }
}

