package com.ministerio.magia.service;

import com.ministerio.magia.model.Artefacto;
import com.ministerio.magia.model.Hechizo;
import com.ministerio.magia.model.RegistroMagico;
import com.ministerio.magia.repository.ArtefactoRepository;
import com.ministerio.magia.repository.HechizoRepository;
import com.ministerio.magia.repository.RegistroMagicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DatosMagicosService {
    
    private static final Logger logger = LoggerFactory.getLogger(DatosMagicosService.class);
    
    @Autowired
    private HechizoRepository hechizoRepository;
    
    @Autowired
    private ArtefactoRepository artefactoRepository;
    
    @Autowired
    private RegistroMagicoRepository registroMagicoRepository;
    
    @Transactional
    public Hechizo crearHechizo(String nombre, String tipo, Integer nivelPoder, String descripcion) {
        Hechizo hechizo = new Hechizo();
        hechizo.setNombre(nombre);
        hechizo.setTipo(tipo);
        hechizo.setNivelPoder(nivelPoder);
        hechizo.setDescripcion(descripcion);
        hechizo.setFechaCreacion(LocalDateTime.now());
        hechizo.setEstado("ACTIVO");
        hechizo.setProcesado(false);
        
        return hechizoRepository.save(hechizo);
    }
    
    @Transactional
    public Artefacto crearArtefacto(String nombre, String categoria, Integer nivelMagia, String ubicacion) {
        Artefacto artefacto = new Artefacto();
        artefacto.setNombre(nombre);
        artefacto.setCategoria(categoria);
        artefacto.setNivelMagia(nivelMagia);
        artefacto.setUbicacion(ubicacion);
        artefacto.setFechaRegistro(LocalDateTime.now());
        artefacto.setEstado("REGISTRADO");
        artefacto.setProcesado(false);
        
        return artefactoRepository.save(artefacto);
    }
    
    @Transactional
    public RegistroMagico crearRegistroMagico(String tipoRegistro, String identificador, String datos) {
        RegistroMagico registro = new RegistroMagico();
        registro.setTipoRegistro(tipoRegistro);
        registro.setIdentificador(identificador);
        registro.setDatos(datos);
        registro.setFechaProcesamiento(LocalDateTime.now());
        registro.setEstado("PENDIENTE");
        registro.setIntentos(0);
        
        return registroMagicoRepository.save(registro);
    }
    
    public List<Hechizo> obtenerHechizosPendientes() {
        return hechizoRepository.findHechizosPendientes();
    }
    
    public List<Artefacto> obtenerArtefactosPendientes() {
        return artefactoRepository.findArtefactosPendientes();
    }
    
    public List<RegistroMagico> obtenerRegistrosPendientes() {
        return registroMagicoRepository.findRegistrosPendientes();
    }
    
    public void inicializarDatosEjemplo() {
        logger.info("Inicializando datos de ejemplo...");
        
        // Crear hechizos de ejemplo
        crearHechizo("Expelliarmus", "Defensivo", 45, "Desarma al oponente");
        crearHechizo("Lumos", "Iluminación", 20, "Crea luz en la punta de la varita");
        crearHechizo("Avada Kedavra", "Maldición", 95, "Maldición asesina");
        crearHechizo("Expecto Patronum", "Defensivo", 80, "Conjura un patronus protector");
        crearHechizo("Wingardium Leviosa", "Encantamiento", 30, "Hace levitar objetos");
        
        // Crear artefactos de ejemplo
        crearArtefacto("Piedra Filosofal", "Alquimia", 100, "Cámara Secreta");
        crearArtefacto("Varita de Saúco", "Varita", 95, "Mansión Malfoy");
        crearArtefacto("Capa de Invisibilidad", "Protección", 90, "Torre de Gryffindor");
        crearArtefacto("Cáliz de Fuego", "Competición", 75, "Gran Salón");
        crearArtefacto("Horrocrux", "Oscuro", 85, "Desconocida");
        
        // Crear registros mágicos de ejemplo
        crearRegistroMagico("HECHIZO", "HECH-001", "Registro de hechizo Expelliarmus");
        crearRegistroMagico("ARTEFACTO", "ART-001", "Registro de artefacto Piedra Filosofal");
        crearRegistroMagico("EVENTO", "EVT-001", "Registro de evento mágico importante");
        crearRegistroMagico("HECHIZO", "HECH-002", "Registro de hechizo Lumos");
        crearRegistroMagico("ARTEFACTO", "ART-002", "Registro de artefacto Varita de Saúco");
        
        logger.info("Datos de ejemplo inicializados correctamente");
    }
}

