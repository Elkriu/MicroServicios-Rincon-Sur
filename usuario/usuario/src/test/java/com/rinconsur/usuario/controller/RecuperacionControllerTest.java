package com.rinconsur.usuario.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rinconsur.usuario.dto.CodigoResponse;
import com.rinconsur.usuario.dto.RestablecerContrasenaRequest;
import com.rinconsur.usuario.dto.SolicitarCodigoRequest;
import com.rinconsur.usuario.dto.VerificarCodigoRequest;
import com.rinconsur.usuario.service.RecuperacionService;

@ExtendWith(MockitoExtension.class)
class RecuperacionControllerTest {

    @Mock
    private RecuperacionService recuperacionService;

    @InjectMocks
    private RecuperacionController recuperacionController;

    @Test
    void debeSolicitarCodigo() {
        SolicitarCodigoRequest req = new SolicitarCodigoRequest("juan@rinconsur.cl");
        CodigoResponse resp = new CodigoResponse("Código generado", "123456");
        when(recuperacionService.solicitar(req)).thenReturn(resp);

        CodigoResponse resultado = recuperacionController.solicitar(req);

        assertSame(resp, resultado);
        verify(recuperacionService, times(1)).solicitar(req);
    }

    @Test
    void debeVerificarCodigo() {
        VerificarCodigoRequest req = new VerificarCodigoRequest("juan@rinconsur.cl", "123456");

        recuperacionController.verificar(req);

        verify(recuperacionService, times(1)).verificar(req);
    }

    @Test
    void debeRestablecerContrasena() {
        RestablecerContrasenaRequest req = new RestablecerContrasenaRequest("juan@rinconsur.cl", "123456", "NuevaClave1!");

        recuperacionController.restablecer(req);

        verify(recuperacionService, times(1)).restablecer(req);
    }
}