package com.rinconsur.visitante.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un registro de conteo. Se usa al guardar un registro nuevo en una sesión y al editar uno existente")
public record GuardarRegistroRequest(

        @Schema(description = "Clasificación de la persona. La app envía TRANSITO, VISITA o NO_DETERMINADO. El servicio no valida estos valores",
                example = "VISITA", requiredMode = Schema.RequiredMode.REQUIRED)
        String clasificacion,

        @Schema(description = "Rango de edad. La app envía MENOR_18, EDAD_18_25, EDAD_26_40, EDAD_41_60 o MAYOR_61. El servicio no valida estos valores",
                example = "EDAD_26_40", requiredMode = Schema.RequiredMode.REQUIRED)
        String rangoEtario,

        @Schema(description = "Con quién viene. La app envía SOLO, PAREJA, CON_NINOS o GRUPO. El servicio no valida estos valores",
                example = "PAREJA", requiredMode = Schema.RequiredMode.REQUIRED)
        String compania,

        @Schema(description = "true si trae bolsas de otra tienda", example = "false")
        boolean bolsas,

        @Schema(description = "Acceso al que se dirige (A1 a A4). Solo aplica a las personas en TRANSITO; en una VISITA se omite",
                example = "A3", nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String destino,

        @Schema(description = "Hora del registro en formato HH:mm:ss. Obligatoria al guardar un registro nuevo; al editar es opcional "
                + "y, si no se envía, se conserva la hora original",
                example = "10:05:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String hora
) {
}