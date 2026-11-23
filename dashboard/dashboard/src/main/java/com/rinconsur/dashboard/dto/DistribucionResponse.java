package com.rinconsur.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Distribución de los registros por clasificación, rango etario, compañía y bolsas. "
        + "Cada lista va ordenada de mayor a menor cantidad y, si empatan, alfabéticamente")
public record DistribucionResponse(

        @Schema(description = "Fecha usada como filtro (dd-MM-yyyy). Es null si no se filtró por fecha",
                example = "01-11-2026", nullable = true)
        String fecha,

        @Schema(description = "Acceso usado como filtro. Es null si no se filtró por acceso",
                example = "Acceso A1", nullable = true)
        String acceso,

        @Schema(description = "Total de registros considerados", example = "12")
        long total,

        @Schema(description = "Registros por clasificación (VISITA, TRANSITO, etc.)")
        List<ItemDistribucionResponse> porClasificacion,

        @Schema(description = "Registros por rango etario, agrupados por el texto tal como lo guardó el servicio de visitantes")
        List<ItemDistribucionResponse> porRangoEtario,

        @Schema(description = "Registros por compañía (SOLO, PAREJA, CON_NINOS, GRUPO)")
        List<ItemDistribucionResponse> porCompania,

        @Schema(description = "Registros según si trae bolsas de otra tienda. Las etiquetas son Sí trae y No trae")
        List<ItemDistribucionResponse> porBolsas
) {
}