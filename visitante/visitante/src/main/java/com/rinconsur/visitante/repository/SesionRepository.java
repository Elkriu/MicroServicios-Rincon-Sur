package com.rinconsur.visitante.repository;

import com.rinconsur.visitante.model.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SesionRepository extends JpaRepository<Sesion, Long> {

    List<Sesion> findByUsuarioIdOrderByIdDesc(Long usuarioId);

    boolean existsByUsuarioIdAndEstadoNot(Long usuarioId, String estado);

    boolean existsByAccesoAndEstadoNot(String acceso, String estado);

    boolean existsByCodigo(String codigo);

    long countByUsuarioId(Long usuarioId);

    long countByUsuarioIdAndEstado(Long usuarioId, String estado);
}