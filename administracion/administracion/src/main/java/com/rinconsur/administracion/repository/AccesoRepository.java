package com.rinconsur.administracion.repository;

import com.rinconsur.administracion.model.Acceso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccesoRepository extends JpaRepository<Acceso, String> {

    List<Acceso> findAllByOrderByNombreAsc();

    List<Acceso> findByDisponibleOrderByNombreAsc(boolean disponible);
}