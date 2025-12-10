package com.ministerio.magia.batch.writer;

import com.ministerio.magia.model.Artefacto;
import com.ministerio.magia.repository.ArtefactoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArtefactoItemWriter implements ItemWriter<Artefacto> {
    
    private static final Logger logger = LoggerFactory.getLogger(ArtefactoItemWriter.class);
    
    @Autowired
    private ArtefactoRepository artefactoRepository;
    
    @Override
    public void write(Chunk<? extends Artefacto> chunk) throws Exception {
        logger.info("Escribiendo {} artefactos procesados", chunk.size());
        
        for (Artefacto artefacto : chunk) {
            artefacto.setProcesado(true);
            artefacto.setEstado("PROCESADO");
            artefactoRepository.save(artefacto);
            logger.debug("Artefacto procesado y guardado: {}", artefacto.getNombre());
        }
        
        logger.info("Artefactos escritos exitosamente");
    }
}

