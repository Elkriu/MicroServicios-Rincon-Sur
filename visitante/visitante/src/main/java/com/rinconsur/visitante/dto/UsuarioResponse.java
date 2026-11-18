package com.rinconsur.visitante.dto;

public record UsuarioResponse(
        Long id,
        String nombre,
        String correo,
        Long rolId,
        String rolNombre,
        String fotoPerfilUrl,
        boolean activo
) {
}