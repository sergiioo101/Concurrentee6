package com.ministerio.magia.batch.reader;

import com.ministerio.magia.model.Hechizo;
import com.ministerio.magia.repository.HechizoRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class HechizoItemReader implements ItemReader<Hechizo> {
    
    @Autowired
    private HechizoRepository hechizoRepository;
    
    private Iterator<Hechizo> hechizoIterator;
    private boolean initialized = false;
    
    @Override
    public Hechizo read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (!initialized) {
            List<Hechizo> hechizos = hechizoRepository.findHechizosPendientes();
            hechizoIterator = hechizos.iterator();
            initialized = true;
        }
        
        if (hechizoIterator != null && hechizoIterator.hasNext()) {
            return hechizoIterator.next();
        }
        
        return null; // Indica fin de lectura
    }
}

