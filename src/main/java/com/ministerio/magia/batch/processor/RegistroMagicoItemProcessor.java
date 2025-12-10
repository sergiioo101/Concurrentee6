package com.ministerio.magia.batch.processor;

import com.ministerio.magia.model.RegistroMagico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RegistroMagicoItemProcessor implements ItemProcessor<RegistroMagico, RegistroMagico> {
    
    private static final Logger logger = LoggerFactory.getLogger(RegistroMagicoItemProcessor.class);
    
    @Override
    public RegistroMagico process(RegistroMagico registro) throws Exception {
        logger.info("Procesando registro mágico: {} - {}", registro.getTipoRegistro(), registro.getIdentificador());
        
        // Validación del registro
        if (registro.getDatos() == null || registro.getDatos().isEmpty()) {
            logger.warn("Registro sin datos: {}", registro.getIdentificador());
            throw new IllegalArgumentException("Registro sin datos");
        }
        
        // Actualizar fecha de procesamiento
        registro.setFechaProcesamiento(LocalDateTime.now());
        
        // Validar tipo de registro
        if (!registro.getTipoRegistro().matches("HECHIZO|ARTEFACTO|EVENTO")) {
            logger.warn("Tipo de registro inválido: {}", registro.getTipoRegistro());
            throw new IllegalArgumentException("Tipo de registro inválido");
        }
        
        return registro;
    }
}

