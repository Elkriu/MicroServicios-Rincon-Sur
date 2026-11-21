package com.rinconsur.usuario.controller;

import com.rinconsur.usuario.dto.CodigoResponse;
import com.rinconsur.usuario.dto.RestablecerContrasenaRequest;
import com.rinconsur.usuario.dto.SolicitarCodigoRequest;
import com.rinconsur.usuario.dto.VerificarCodigoRequest;
import com.rinconsur.usuario.service.RecuperacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Recuperación de clave",
        description = "Flujo de Olvidé mi contraseña en 3 pasos: 1) solicitar el código, 2) verificarlo y "
                + "3) restablecer la clave. El código tiene 6 dígitos, vence a los 10 minutos y sirve una sola vez")
@RestController
@RequestMapping("/api/usuarios/recuperacion")
@RequiredArgsConstructor
public class RecuperacionController {

    private final RecuperacionService recuperacionService;

    @Operation(summary = "Paso 1: solicitar código de recuperación",
            description = "Genera un código de 6 dígitos para el correo indicado. Todavía no se envía por correo: "
                    + "queda en la consola del servicio y, si app.recuperacion.devolver-codigo=true, también en la respuesta. "
                    + "Pedir un código nuevo reemplaza al anterior.")
    @ApiResponse(responseCode = "200", description = "Código generado")
    @ApiResponse(responseCode = "400", description = "Correo vacío, mal formado o fuera del dominio @rinconsur.cl")
    @ApiResponse(responseCode = "403", description = "La cuenta está suspendida")
    @ApiResponse(responseCode = "404", description = "La cuenta no está registrada")
    @PostMapping("/solicitar")
    public CodigoResponse solicitar(@RequestBody SolicitarCodigoRequest datos) {
        return recuperacionService.solicitar(datos);
    }

    @Operation(summary = "Paso 2: verificar el código",
            description = "Solo comprueba que el código sea correcto y no haya vencido. No lo gasta: "
                    + "sirve para que la app pase a la pantalla de la clave nueva.")
    @ApiResponse(responseCode = "204", description = "Código correcto (sin contenido en la respuesta)")
    @ApiResponse(responseCode = "400", description = "El código no tiene 6 dígitos, es incorrecto o ya venció")
    @PostMapping("/verificar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verificar(@RequestBody VerificarCodigoRequest datos) {
        recuperacionService.verificar(datos);
    }

    @Operation(summary = "Paso 3: restablecer la clave",
            description = "Vuelve a comprobar el código, valida el formato de la clave nueva y la guarda. "
                    + "El código se gasta y no se puede reutilizar.")
    @ApiResponse(responseCode = "204", description = "Clave cambiada (sin contenido en la respuesta)")
    @ApiResponse(responseCode = "400", description = "Código incorrecto o vencido, o la clave nueva no cumple el formato")
    @ApiResponse(responseCode = "403", description = "La cuenta está suspendida")
    @PostMapping("/restablecer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restablecer(@RequestBody RestablecerContrasenaRequest datos) {
        recuperacionService.restablecer(datos);
    }
}