package com.rinconsur.usuario.repository;

import com.rinconsur.usuario.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {
}