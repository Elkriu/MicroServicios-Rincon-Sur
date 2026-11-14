package com.rinconsur.usuario.dto;

/** "codigo" viene null cuando app.recuperacion.devolver-codigo=false. */
public record CodigoResponse(String mensaje, String codigo) {
}