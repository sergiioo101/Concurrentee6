package com.ministerio.magia.repository;

import com.ministerio.magia.model.RegistroMagico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroMagicoRepository extends JpaRepository<RegistroMagico, Long> {
    
    @Query("SELECT r FROM RegistroMagico r WHERE r.estado = 'PENDIENTE'")
    List<RegistroMagico> findRegistrosPendientes();
    
    List<RegistroMagico> findByEstado(String estado);
}

