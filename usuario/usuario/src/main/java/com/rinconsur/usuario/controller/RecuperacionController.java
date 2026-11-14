package com.rinconsur.usuario.controller;

import com.rinconsur.usuario.dto.CodigoResponse;
import com.rinconsur.usuario.dto.RestablecerContrasenaRequest;
import com.rinconsur.usuario.dto.SolicitarCodigoRequest;
import com.rinconsur.usuario.dto.VerificarCodigoRequest;
import com.rinconsur.usuario.service.RecuperacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios/recuperacion")
@RequiredArgsConstructor
public class RecuperacionController {

    private final RecuperacionService recuperacionService;

    @PostMapping("/solicitar")
    public CodigoResponse solicitar(@RequestBody SolicitarCodigoRequest datos) {
        return recuperacionService.solicitar(datos);
    }

    @PostMapping("/verificar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verificar(@RequestBody VerificarCodigoRequest datos) {
        recuperacionService.verificar(datos);
    }

    @PostMapping("/restablecer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restablecer(@RequestBody RestablecerContrasenaRequest datos) {
        recuperacionService.restablecer(datos);
    }
}