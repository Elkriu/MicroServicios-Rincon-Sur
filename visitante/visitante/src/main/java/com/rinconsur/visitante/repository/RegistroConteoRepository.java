package com.rinconsur.visitante.repository;

import com.rinconsur.visitante.model.RegistroConteo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegistroConteoRepository extends JpaRepository<RegistroConteo, Long> {

    List<RegistroConteo> findBySesionIdOrderByIdAsc(Long sesionId);

    long countBySesionId(Long sesionId);

    long countBySesionUsuarioId(Long usuarioId);

    Optional<RegistroConteo> findFirstBySesionIdOrderByIdDesc(Long sesionId);

    @Query("select r from RegistroConteo r join fetch r.sesion s "
            + "where (:fecha is null or s.fecha = :fecha) "
            + "and (:acceso is null or s.acceso = :acceso) "
            + "order by r.id asc")
    List<RegistroConteo> buscar(@Param("fecha") String fecha, @Param("acceso") String acceso);

    @Query("select distinct r.sesion.fecha from RegistroConteo r")
    List<String> fechasConRegistros();
}