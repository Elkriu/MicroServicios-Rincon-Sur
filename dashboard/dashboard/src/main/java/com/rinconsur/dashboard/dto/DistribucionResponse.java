package com.rinconsur.dashboard.dto;

import java.util.List;

public record DistribucionResponse(
        String fecha,
        String acceso,
        long total,
        List<ItemDistribucionResponse> porClasificacion,
        List<ItemDistribucionResponse> porRangoEtario,
        List<ItemDistribucionResponse> porCompania,
        List<ItemDistribucionResponse> porBolsas
) {
}