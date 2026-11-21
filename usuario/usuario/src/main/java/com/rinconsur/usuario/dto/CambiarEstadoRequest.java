package com.rinconsur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Suspender o reactivar una cuenta")
public record CambiarEstadoRequest(

        @Schema(description = "false = suspender la cuenta, true = reactivarla", example = "false",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean activo) {
}