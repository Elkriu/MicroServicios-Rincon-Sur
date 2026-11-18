package com.rinconsur.visitante.dto;

public record CrearSesionRequest(
        Long usuarioId,
        String acceso
) {
}