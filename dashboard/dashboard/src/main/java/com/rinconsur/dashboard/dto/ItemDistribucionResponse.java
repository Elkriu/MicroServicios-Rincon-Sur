package com.rinconsur.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Una fila de un conteo agrupado: un valor y cuántos registros lo tienen")
public record ItemDistribucionResponse(

        @Schema(description = "Valor agrupado: una clasificación, un rango etario, una compañía, un acceso o, en las bolsas, "
                + "Sí trae / No trae", example = "VISITA")
        String etiqueta,

        @Schema(description = "Cantidad de registros con ese valor", example = "8")
        long cantidad
) {
}