package com.rinconsur.visitante.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Registro de conteo de una sesión")
public record RegistroResponse(

        @Schema(description = "Id del registro", example = "1")
        Long id,

        @Schema(description = "Hora del registro en formato HH:mm:ss", example = "10:05:00")
        String hora,

        @Schema(description = "Clasificación de la persona", example = "VISITA")
        String clasificacion,

        @Schema(description = "Rango de edad", example = "EDAD_26_40")
        String rangoEtario,

        @Schema(description = "Con quién viene", example = "SOLO")
        String compania,

        @Schema(description = "true si trae bolsas de otra tienda", example = "false")
        boolean bolsas,

        @Schema(description = "Acceso al que se dirige. Es null cuando no aplica", example = "A3", nullable = true)
        String destino
) {
}