package com.ministerio.magia.batch.reader;

import com.ministerio.magia.model.Artefacto;
import com.ministerio.magia.repository.ArtefactoRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class ArtefactoItemReader implements ItemReader<Artefacto> {
    
    @Autowired
    private ArtefactoRepository artefactoRepository;
    
    private Iterator<Artefacto> artefactoIterator;
    private boolean initialized = false;
    
    @Override
    public Artefacto read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (!initialized) {
            List<Artefacto> artefactos = artefactoRepository.findArtefactosPendientes();
            artefactoIterator = artefactos.iterator();
            initialized = true;
        }
        
        if (artefactoIterator != null && artefactoIterator.hasNext()) {
            return artefactoIterator.next();
        }
        
        return null; // Indica fin de lectura
    }
}

