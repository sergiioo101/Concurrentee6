package com.ministerio.magia.repository;

import com.ministerio.magia.model.Artefacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtefactoRepository extends JpaRepository<Artefacto, Long> {
    
    List<Artefacto> findByProcesadoFalse();
    
    @Query("SELECT a FROM Artefacto a WHERE a.estado = 'REGISTRADO' AND a.procesado = false")
    List<Artefacto> findArtefactosPendientes();
}

