package com.ministerio.magia.controller;

import com.ministerio.magia.service.JobService;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    
    @Autowired
    private JobService jobService;
    
    @PostMapping("/procesar-todos")
    public ResponseEntity<Map<String, Object>> ejecutarProcesarTodos() {
        JobExecution execution = jobService.ejecutarProcesarDatosMagicosJob();
        return crearRespuesta(execution, "Job de procesamiento completo iniciado");
    }
    
    @PostMapping("/procesar-hechizos")
    public ResponseEntity<Map<String, Object>> ejecutarProcesarHechizos() {
        JobExecution execution = jobService.ejecutarProcesarHechizosJob();
        return crearRespuesta(execution, "Job de procesamiento de hechizos iniciado");
    }
    
    @PostMapping("/procesar-artefactos")
    public ResponseEntity<Map<String, Object>> ejecutarProcesarArtefactos() {
        JobExecution execution = jobService.ejecutarProcesarArtefactosJob();
        return crearRespuesta(execution, "Job de procesamiento de artefactos iniciado");
    }
    
    @PostMapping("/procesar-registros")
    public ResponseEntity<Map<String, Object>> ejecutarProcesarRegistros() {
        JobExecution execution = jobService.ejecutarProcesarRegistrosMagicosJob();
        return crearRespuesta(execution, "Job de procesamiento de registros mágicos iniciado");
    }
    
    private ResponseEntity<Map<String, Object>> crearRespuesta(JobExecution execution, String mensaje) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", mensaje);
        respuesta.put("jobId", execution.getId());
        respuesta.put("jobName", execution.getJobInstance().getJobName());
        respuesta.put("status", execution.getStatus().toString());
        return ResponseEntity.ok(respuesta);
    }
}

