package com.rinconsur.visitante.dto;

public record GuardarRegistroRequest(
        String clasificacion,
        String rangoEtario,
        String compania,
        boolean bolsas,
        String destino,
        String hora
) {
}