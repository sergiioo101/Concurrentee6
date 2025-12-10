package com.ministerio.magia.batch.reader;

import com.ministerio.magia.model.RegistroMagico;
import com.ministerio.magia.repository.RegistroMagicoRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class RegistroMagicoItemReader implements ItemReader<RegistroMagico> {
    
    @Autowired
    private RegistroMagicoRepository registroMagicoRepository;
    
    private Iterator<RegistroMagico> registroIterator;
    private boolean initialized = false;
    
    @Override
    public RegistroMagico read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (!initialized) {
            List<RegistroMagico> registros = registroMagicoRepository.findRegistrosPendientes();
            registroIterator = registros.iterator();
            initialized = true;
        }
        
        if (registroIterator != null && registroIterator.hasNext()) {
            return registroIterator.next();
        }
        
        return null; // Indica fin de lectura
    }
}

