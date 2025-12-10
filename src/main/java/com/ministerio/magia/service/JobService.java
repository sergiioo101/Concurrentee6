package com.ministerio.magia.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JobService {
    
    private static final Logger logger = LoggerFactory.getLogger(JobService.class);
    
    @Autowired
    private JobLauncher jobLauncher;
    
    @Autowired
    @Qualifier("procesarDatosMagicosJob")
    private Job procesarDatosMagicosJob;
    
    @Autowired
    @Qualifier("procesarHechizosJob")
    private Job procesarHechizosJob;
    
    @Autowired
    @Qualifier("procesarArtefactosJob")
    private Job procesarArtefactosJob;
    
    @Autowired
    @Qualifier("procesarRegistrosMagicosJob")
    private Job procesarRegistrosMagicosJob;
    
    public JobExecution ejecutarProcesarDatosMagicosJob() {
        return ejecutarJob(procesarDatosMagicosJob, "Procesar todos los datos mágicos");
    }
    
    public JobExecution ejecutarProcesarHechizosJob() {
        return ejecutarJob(procesarHechizosJob, "Procesar hechizos");
    }
    
    public JobExecution ejecutarProcesarArtefactosJob() {
        return ejecutarJob(procesarArtefactosJob, "Procesar artefactos");
    }
    
    public JobExecution ejecutarProcesarRegistrosMagicosJob() {
        return ejecutarJob(procesarRegistrosMagicosJob, "Procesar registros mágicos");
    }
    
    private JobExecution ejecutarJob(Job job, String descripcion) {
        try {
            logger.info("Iniciando job: {}", descripcion);
            
            Map<String, JobParameter<?>> parameters = new HashMap<>();
            parameters.put("time", new JobParameter<>(System.currentTimeMillis(), Long.class));
            
            JobParameters jobParameters = new JobParameters(parameters);
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            
            logger.info("Job {} iniciado con ID: {}", descripcion, jobExecution.getId());
            return jobExecution;
            
        } catch (JobExecutionAlreadyRunningException e) {
            logger.error("El job ya está en ejecución: {}", descripcion, e);
            throw new RuntimeException("El job ya está en ejecución", e);
        } catch (JobRestartException e) {
            logger.error("Error al reiniciar el job: {}", descripcion, e);
            throw new RuntimeException("Error al reiniciar el job", e);
        } catch (JobInstanceAlreadyCompleteException e) {
            logger.error("El job ya se completó: {}", descripcion, e);
            throw new RuntimeException("El job ya se completó", e);
        } catch (JobParametersInvalidException e) {
            logger.error("Parámetros de job inválidos: {}", descripcion, e);
            throw new RuntimeException("Parámetros de job inválidos", e);
        } catch (Exception e) {
            logger.error("Error inesperado al ejecutar el job: {}", descripcion, e);
            throw new RuntimeException("Error inesperado al ejecutar el job", e);
        }
    }
}

