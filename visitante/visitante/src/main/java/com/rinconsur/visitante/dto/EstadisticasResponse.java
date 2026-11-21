package com.rinconsur.visitante.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen de la actividad de un observador")
public record EstadisticasResponse(

        @Schema(description = "Total de sesiones del observador, terminadas o no", example = "3")
        long totalSesiones,

        @Schema(description = "Sesiones del observador que ya están finalizadas", example = "2")
        long sesionesCompletadas,

        @Schema(description = "Total de registros de conteo (muestras) en todas las sesiones del observador", example = "12")
        long muestrasRegistradas
) {
}