package com.rinconsur.administracion.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Acceso (entrada) de la tienda")
public record AccesoResponse(

        @Schema(description = "Nombre del acceso. Es su identificador", example = "Acceso A1")
        String nombre,

        @Schema(description = "true = el acceso está abierto y se puede usar para sesiones de conteo, false = está cerrado",
                example = "true")
        boolean disponible
) {
}