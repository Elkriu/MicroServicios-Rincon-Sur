package com.rinconsur.visitante.dto;

public record SesionResponse(
        Long id,
        String codigo,
        String acceso,
        String fecha,
        String estado,
        Long usuarioId,
        int cantidadRegistros
) {
}