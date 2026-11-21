package com.rinconsur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** "codigo" viene null cuando app.recuperacion.devolver-codigo=false. */
@Schema(description = "Resultado de pedir un código de recuperación")
public record CodigoResponse(

        @Schema(description = "Mensaje informativo con la vigencia del código",
                example = "Código generado. Vence en 10 minutos.")
        String mensaje,

        @Schema(description = "Código de 6 dígitos. Solo viaja en la respuesta mientras app.recuperacion.devolver-codigo=true "
                + "(modo pruebas); si está en false viene null",
                example = "123456", nullable = true)
        String codigo) {
}