package com.rinconsur.visitante.dto;

public record EstadisticasResponse(
        long totalSesiones,
        long sesionesCompletadas,
        long muestrasRegistradas
) {
}