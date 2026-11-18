package com.rinconsur.visitante.repository;

import com.rinconsur.visitante.model.RegistroConteo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistroConteoRepository extends JpaRepository<RegistroConteo, Long> {

    List<RegistroConteo> findBySesionIdOrderByIdAsc(Long sesionId);

    long countBySesionId(Long sesionId);

    long countBySesionUsuarioId(Long usuarioId);

    Optional<RegistroConteo> findFirstBySesionIdOrderByIdDesc(Long sesionId);
}