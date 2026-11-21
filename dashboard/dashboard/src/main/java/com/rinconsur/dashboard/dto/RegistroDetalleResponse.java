package com.rinconsur.dashboard.dto;

public record RegistroDetalleResponse(
        Long id,
        Long sesionId,
        String acceso,
        String fecha,
        String hora,
        String clasificacion,
        String rangoEtario,
        String compania,
        boolean bolsas,
        String destino
) {
}