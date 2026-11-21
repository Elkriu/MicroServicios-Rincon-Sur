package com.rinconsur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Rol de usuario")
public record RolResponse(

        @Schema(description = "Id del rol", example = "1")
        Long id,

        @Schema(description = "Nombre del rol", example = "Observador de Pasillo")
        String nombre) {
}