package com.ministerio.magia.batch.writer;

import com.ministerio.magia.model.Hechizo;
import com.ministerio.magia.repository.HechizoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HechizoItemWriter implements ItemWriter<Hechizo> {
    
    private static final Logger logger = LoggerFactory.getLogger(HechizoItemWriter.class);
    
    @Autowired
    private HechizoRepository hechizoRepository;
    
    @Override
    public void write(Chunk<? extends Hechizo> chunk) throws Exception {
        logger.info("Escribiendo {} hechizos procesados", chunk.size());
        
        for (Hechizo hechizo : chunk) {
            hechizo.setProcesado(true);
            hechizo.setEstado("ACTIVO");
            hechizoRepository.save(hechizo);
            logger.debug("Hechizo procesado y guardado: {}", hechizo.getNombre());
        }
        
        logger.info("Hechizos escritos exitosamente");
    }
}

