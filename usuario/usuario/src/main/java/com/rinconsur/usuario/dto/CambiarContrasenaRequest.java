package com.rinconsur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cambio de clave con la sesión iniciada")
public record CambiarContrasenaRequest(

        @Schema(description = "Clave actual del usuario", example = "Rincor.2026!",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String actual,

        @Schema(description = "Clave nueva: mínimo 8 caracteres con mayúscula, minúscula y un símbolo (@#$%^&+=!._-)",
                example = "Nueva2026.", requiredMode = Schema.RequiredMode.REQUIRED)
        String nueva) {
}