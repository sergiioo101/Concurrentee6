package com.ministerio.magia.batch.processor;

import com.ministerio.magia.model.Artefacto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ArtefactoItemProcessor implements ItemProcessor<Artefacto, Artefacto> {
    
    private static final Logger logger = LoggerFactory.getLogger(ArtefactoItemProcessor.class);
    
    @Override
    public Artefacto process(Artefacto artefacto) throws Exception {
        logger.info("Procesando artefacto: {}", artefacto.getNombre());
        
        // Validación y transformación del artefacto
        if (artefacto.getNivelMagia() < 0 || artefacto.getNivelMagia() > 100) {
            logger.warn("Nivel de magia inválido para artefacto: {}", artefacto.getNombre());
            throw new IllegalArgumentException("Nivel de magia inválido");
        }
        
        // Marcar como en análisis
        artefacto.setEstado("EN_ANALISIS");
        
        // Simular procesamiento adicional
        if (artefacto.getNivelMagia() > 70) {
            logger.info("Artefacto de alto nivel detectado: {}", artefacto.getNombre());
        }
        
        return artefacto;
    }
}

