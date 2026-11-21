package com.rinconsur.visitante.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para iniciar una sesión de conteo")
public record CrearSesionRequest(

        @Schema(description = "Id del observador (usuario del servicio de usuarios). Debe tener rol Observador de Pasillo "
                + "y la cuenta activa",
                example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long usuarioId,

        @Schema(description = "Nombre del acceso donde se hará el conteo, tal como lo registra el servicio de administración. "
                + "Debe existir y estar disponible",
                example = "Acceso A1", requiredMode = Schema.RequiredMode.REQUIRED)
        String acceso
) {
}