package com.rinconsur.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Registro de conteo tal como lo entrega el servicio de visitantes")
public record RegistroDetalleResponse(

        @Schema(description = "Id del registro", example = "1")
        Long id,

        @Schema(description = "Id de la sesión a la que pertenece", example = "1")
        Long sesionId,

        @Schema(description = "Acceso donde se hizo el conteo", example = "Acceso A1")
        String acceso,

        @Schema(description = "Fecha de la sesión en formato dd-MM-yyyy", example = "01-11-2026")
        String fecha,

        @Schema(description = "Hora del registro en formato HH:mm:ss", example = "10:05:00")
        String hora,

        @Schema(description = "Clasificación de la persona", example = "VISITA")
        String clasificacion,

        @Schema(description = "Rango de edad, tal como lo guardó el servicio de visitantes", example = "RANGO_26_40")
        String rangoEtario,

        @Schema(description = "Con quién viene", example = "SOLO")
        String compania,

        @Schema(description = "true si trae bolsas de otra tienda", example = "false")
        boolean bolsas,

        @Schema(description = "Acceso al que se dirige. Es null cuando no aplica", example = "A3", nullable = true)
        String destino
) {
}