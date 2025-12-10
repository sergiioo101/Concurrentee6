package com.ministerio.magia.batch.writer;

import com.ministerio.magia.model.RegistroMagico;
import com.ministerio.magia.repository.RegistroMagicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegistroMagicoItemWriter implements ItemWriter<RegistroMagico> {
    
    private static final Logger logger = LoggerFactory.getLogger(RegistroMagicoItemWriter.class);
    
    @Autowired
    private RegistroMagicoRepository registroMagicoRepository;
    
    @Override
    public void write(Chunk<? extends RegistroMagico> chunk) throws Exception {
        logger.info("Escribiendo {} registros mágicos procesados", chunk.size());
        
        for (RegistroMagico registro : chunk) {
            registro.setEstado("PROCESADO");
            registroMagicoRepository.save(registro);
            logger.debug("Registro mágico procesado y guardado: {}", registro.getIdentificador());
        }
        
        logger.info("Registros mágicos escritos exitosamente");
    }
}

