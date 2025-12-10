package com.ministerio.magia.controller;

import com.ministerio.magia.model.Artefacto;
import com.ministerio.magia.model.Hechizo;
import com.ministerio.magia.model.RegistroMagico;
import com.ministerio.magia.service.DatosMagicosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/datos-magicos")
public class DatosMagicosController {
    
    @Autowired
    private DatosMagicosService datosMagicosService;
    
    @PostMapping("/inicializar")
    public ResponseEntity<Map<String, String>> inicializarDatos() {
        datosMagicosService.inicializarDatosEjemplo();
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Datos de ejemplo inicializados correctamente");
        return ResponseEntity.ok(respuesta);
    }
    
    @GetMapping("/hechizos/pendientes")
    public ResponseEntity<List<Hechizo>> obtenerHechizosPendientes() {
        return ResponseEntity.ok(datosMagicosService.obtenerHechizosPendientes());
    }
    
    @GetMapping("/artefactos/pendientes")
    public ResponseEntity<List<Artefacto>> obtenerArtefactosPendientes() {
        return ResponseEntity.ok(datosMagicosService.obtenerArtefactosPendientes());
    }
    
    @GetMapping("/registros/pendientes")
    public ResponseEntity<List<RegistroMagico>> obtenerRegistrosPendientes() {
        return ResponseEntity.ok(datosMagicosService.obtenerRegistrosPendientes());
    }
    
    @PostMapping("/hechizos")
    public ResponseEntity<Hechizo> crearHechizo(@RequestBody Map<String, Object> datos) {
        Hechizo hechizo = datosMagicosService.crearHechizo(
            (String) datos.get("nombre"),
            (String) datos.get("tipo"),
            ((Number) datos.get("nivelPoder")).intValue(),
            (String) datos.get("descripcion")
        );
        return ResponseEntity.ok(hechizo);
    }
    
    @PostMapping("/artefactos")
    public ResponseEntity<Artefacto> crearArtefacto(@RequestBody Map<String, Object> datos) {
        Artefacto artefacto = datosMagicosService.crearArtefacto(
            (String) datos.get("nombre"),
            (String) datos.get("categoria"),
            ((Number) datos.get("nivelMagia")).intValue(),
            (String) datos.get("ubicacion")
        );
        return ResponseEntity.ok(artefacto);
    }
    
    @PostMapping("/registros")
    public ResponseEntity<RegistroMagico> crearRegistro(@RequestBody Map<String, Object> datos) {
        RegistroMagico registro = datosMagicosService.crearRegistroMagico(
            (String) datos.get("tipoRegistro"),
            (String) datos.get("identificador"),
            (String) datos.get("datos")
        );
        return ResponseEntity.ok(registro);
    }
}

