package com.rinconsur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para crear una cuenta")
public record CrearUsuarioRequest(

        @Schema(description = "Nombre completo. Solo letras y espacios, máximo 50 caracteres",
                example = "Ana Torres", requiredMode = Schema.RequiredMode.REQUIRED)
        String nombre,

        @Schema(description = "Correo institucional: debe terminar en @rinconsur.cl, máximo 50 caracteres. Se guarda en minúsculas",
                example = "ana@rinconsur.cl", requiredMode = Schema.RequiredMode.REQUIRED)
        String correo,

        @Schema(description = "Clave: mínimo 8 caracteres con mayúscula, minúscula y un símbolo (@#$%^&+=!._-)",
                example = "Secreto1.", requiredMode = Schema.RequiredMode.REQUIRED)
        String contrasena,

        @Schema(description = "Id del rol: 1 Observador de Pasillo, 2 Jefe de Tienda, 3 Administrador. "
                + "Si no se envía, queda como Observador de Pasillo",
                example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Long rolId) {
}