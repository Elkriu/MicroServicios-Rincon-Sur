package com.rinconsur.usuario.repository;

import com.rinconsur.usuario.model.TokenRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, String> {
}