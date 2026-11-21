package com.rinconsur.visitante.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Sesión de conteo: un observador contando visitantes en un acceso")
public record SesionResponse(

        @Schema(description = "Id de la sesión", example = "1")
        Long id,

        @Schema(description = "Código único de la sesión", example = "SES-482")
        String codigo,

        @Schema(description = "Acceso donde se hace el conteo", example = "Acceso A1")
        String acceso,

        @Schema(description = "Fecha de la sesión en formato dd-MM-yyyy", example = "01-11-2026")
        String fecha,

        @Schema(description = "Estado de la sesión: EN_CURSO o FINALIZADA", example = "EN_CURSO",
                allowableValues = {"EN_CURSO", "FINALIZADA"})
        String estado,

        @Schema(description = "Id del observador (usuario del servicio de usuarios) que hace el conteo", example = "1")
        Long usuarioId,

        @Schema(description = "Cantidad de registros de conteo que tiene la sesión", example = "5")
        int cantidadRegistros
) {
}