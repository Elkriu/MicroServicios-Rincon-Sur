package com.rinconsur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos públicos de un usuario. Nunca incluye la clave")
public record UsuarioResponse(

        @Schema(description = "Id del usuario", example = "1")
        Long id,

        @Schema(description = "Nombre completo", example = "Tamara Ramírez")
        String nombre,

        @Schema(description = "Correo institucional", example = "observador@rinconsur.cl")
        String correo,

        @Schema(description = "Id del rol", example = "1")
        Long rolId,

        @Schema(description = "Nombre del rol", example = "Observador de Pasillo")
        String rolNombre,

        @Schema(description = "URL de la foto de perfil. Es null si el usuario aún no sube una",
                example = "http://localhost:8081/api/usuarios/1/foto", nullable = true)
        String fotoPerfilUrl,

        @Schema(description = "true = cuenta activa, false = cuenta suspendida", example = "true")
        boolean activo
) {
}