package com.rinconsur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Paso 3 de la recuperación: poner la clave nueva con el código recibido")
public record RestablecerContrasenaRequest(

        @Schema(description = "Correo de la cuenta", example = "admin@rinconsur.cl",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String correo,

        @Schema(description = "Código de 6 dígitos recibido en el paso 1. Sirve una sola vez", example = "123456",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String codigo,

        @Schema(description = "Clave nueva: mínimo 8 caracteres con mayúscula, minúscula y un símbolo (@#$%^&+=!._-)",
                example = "Otra2026.", requiredMode = Schema.RequiredMode.REQUIRED)
        String nueva) {
}