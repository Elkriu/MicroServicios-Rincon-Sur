package com.rinconsur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Credenciales para iniciar sesión")
public record LoginRequest(

        @Schema(description = "Correo institucional. No distingue mayúsculas", example = "admin@rinconsur.cl",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String correo,

        @Schema(description = "Clave del usuario", example = "Rincor.2026!",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String contrasena) {
}