package com.rinconsur.dashboard.dto;

import java.util.List;

public record ResumenDiarioResponse(
        String fecha,
        long totalMuestras,
        long sesiones,
        List<ItemDistribucionResponse> flujoPorAcceso
) {
}