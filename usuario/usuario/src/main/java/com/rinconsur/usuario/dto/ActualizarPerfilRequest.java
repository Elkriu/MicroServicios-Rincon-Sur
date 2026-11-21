package com.rinconsur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos del perfil que el usuario puede editar")
public record ActualizarPerfilRequest(

        @Schema(description = "Nombre completo. Solo letras y espacios, máximo 50 caracteres",
                example = "Tamara Ramírez", requiredMode = Schema.RequiredMode.REQUIRED)
        String nombre,

        @Schema(description = "Correo institucional (@rinconsur.cl), máximo 50 caracteres. No puede estar usado por otro usuario",
                example = "tamara@rinconsur.cl", requiredMode = Schema.RequiredMode.REQUIRED)
        String correo) {
}