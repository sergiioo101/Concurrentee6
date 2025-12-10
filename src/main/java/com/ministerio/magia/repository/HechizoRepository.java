package com.ministerio.magia.repository;

import com.ministerio.magia.model.Hechizo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HechizoRepository extends JpaRepository<Hechizo, Long> {
    
    List<Hechizo> findByProcesadoFalse();
    
    @Query("SELECT h FROM Hechizo h WHERE h.estado = 'ACTIVO' AND h.procesado = false")
    List<Hechizo> findHechizosPendientes();
}

