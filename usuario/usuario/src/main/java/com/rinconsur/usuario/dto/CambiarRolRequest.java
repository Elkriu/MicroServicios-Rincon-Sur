package com.rinconsur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cambio de rol de un usuario")
public record CambiarRolRequest(

        @Schema(description = "Id del nuevo rol: 1 Observador de Pasillo, 2 Jefe de Tienda, 3 Administrador",
                example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        Long rolId) {
}