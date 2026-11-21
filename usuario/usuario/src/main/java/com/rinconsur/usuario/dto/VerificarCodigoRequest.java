package com.rinconsur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Paso 2 de la recuperación: comprobar el código recibido")
public record VerificarCodigoRequest(

        @Schema(description = "Correo de la cuenta", example = "admin@rinconsur.cl",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String correo,

        @Schema(description = "Código de 6 dígitos recibido en el paso 1", example = "123456",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String codigo) {
}