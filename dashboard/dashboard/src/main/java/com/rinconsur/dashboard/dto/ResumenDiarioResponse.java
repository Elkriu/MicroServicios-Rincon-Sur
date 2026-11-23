package com.rinconsur.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resumen del conteo de un día")
public record ResumenDiarioResponse(

        @Schema(description = "Fecha del resumen en formato dd-MM-yyyy", example = "01-11-2026")
        String fecha,

        @Schema(description = "Total de registros de conteo (muestras) del día", example = "12")
        long totalMuestras,

        @Schema(description = "Cantidad de sesiones distintas que tienen al menos un registro ese día. "
                + "Una sesión sin registros no se cuenta", example = "3")
        long sesiones,

        @Schema(description = "Registros por acceso, ordenados alfabéticamente por el nombre del acceso")
        List<ItemDistribucionResponse> flujoPorAcceso
) {
}