package com.rinconsur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Paso 1 de la recuperación: pedir un código")
public record SolicitarCodigoRequest(

        @Schema(description = "Correo institucional de la cuenta que olvidó su clave", example = "admin@rinconsur.cl",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String correo) {
}