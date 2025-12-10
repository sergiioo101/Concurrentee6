package com.ministerio.magia.batch.processor;

import com.ministerio.magia.model.Hechizo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class HechizoItemProcessor implements ItemProcessor<Hechizo, Hechizo> {
    
    private static final Logger logger = LoggerFactory.getLogger(HechizoItemProcessor.class);
    
    @Override
    public Hechizo process(Hechizo hechizo) throws Exception {
        logger.info("Procesando hechizo: {}", hechizo.getNombre());
        
        // Validación y transformación del hechizo
        if (hechizo.getNivelPoder() < 0 || hechizo.getNivelPoder() > 100) {
            logger.warn("Nivel de poder inválido para hechizo: {}", hechizo.getNombre());
            throw new IllegalArgumentException("Nivel de poder inválido");
        }
        
        // Marcar como en proceso
        hechizo.setEstado("EN_PROCESO");
        
        // Simular procesamiento adicional
        if (hechizo.getNivelPoder() > 80) {
            logger.info("Hechizo de alto poder detectado: {}", hechizo.getNombre());
        }
        
        return hechizo;
    }
}

