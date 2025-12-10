package com.ministerio.magia.config;

import com.ministerio.magia.service.DatosMagicosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private DatosMagicosService datosMagicosService;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Inicializando datos de ejemplo al arrancar la aplicación...");
        datosMagicosService.inicializarDatosEjemplo();
    }
}

