package com.rinconsur.visitante.dto;

public record RegistroResponse(
        Long id,
        String hora,
        String clasificacion,
        String rangoEtario,
        String compania,
        boolean bolsas,
        String destino
) {
}