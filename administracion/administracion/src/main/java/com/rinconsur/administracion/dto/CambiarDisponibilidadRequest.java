package com.rinconsur.administracion.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Abrir o cerrar un acceso")
public record CambiarDisponibilidadRequest(

        @Schema(description = "false = cerrar el acceso, true = abrirlo", example = "false",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean disponible
) {
}