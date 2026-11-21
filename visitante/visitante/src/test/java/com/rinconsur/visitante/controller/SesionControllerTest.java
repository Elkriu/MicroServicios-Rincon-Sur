package com.rinconsur.visitante.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rinconsur.visitante.dto.CrearSesionRequest;
import com.rinconsur.visitante.dto.EstadisticasResponse;
import com.rinconsur.visitante.dto.GuardarRegistroRequest;
import com.rinconsur.visitante.dto.RegistroResponse;
import com.rinconsur.visitante.dto.SesionResponse;
import com.rinconsur.visitante.service.SesionService;

@ExtendWith(MockitoExtension.class)
class SesionControllerTest {

    @Mock
    private SesionService sesionService;

    @InjectMocks
    private SesionController sesionController;

    @Test
    void debeListarSesionesPorUsuario() {
        when(sesionService.listarPorUsuario(10L)).thenReturn(List.of());

        List<SesionResponse> resultado = sesionController.listar(10L);

        assertTrue(resultado.isEmpty());
        verify(sesionService, times(1)).listarPorUsuario(10L);
    }

    @Test
    void debeObtenerEstadisticas() {
        EstadisticasResponse stats = new EstadisticasResponse(5L, 4L, 120L);
        when(sesionService.estadisticas(10L)).thenReturn(stats);

        EstadisticasResponse resultado = sesionController.estadisticas(10L);

        assertSame(stats, resultado);
        verify(sesionService, times(1)).estadisticas(10L);
    }

    @Test
    void debeObtenerSesionPorId() {
        when(sesionService.obtener(1L)).thenReturn(null);

        SesionResponse resultado = sesionController.obtener(1L);

        assertNull(resultado);
        verify(sesionService, times(1)).obtener(1L);
    }

    @Test
    void debeListarSesionesActivas() {
        when(sesionService.listarActivas()).thenReturn(List.of());

        List<SesionResponse> resultado = sesionController.listarActivas();

        assertTrue(resultado.isEmpty());
        verify(sesionService, times(1)).listarActivas();
    }

    @Test
    void debeCrearSesion() {
        CrearSesionRequest req = new CrearSesionRequest(10L, "Puerta Norte");
        when(sesionService.crear(req)).thenReturn(null);

        SesionResponse resultado = sesionController.crear(req);

        assertNull(resultado);
        verify(sesionService, times(1)).crear(req);
    }

    @Test
    void debeFinalizarSesion() {
        when(sesionService.finalizar(3L)).thenReturn(null);

        SesionResponse resultado = sesionController.finalizar(3L);

        assertNull(resultado);
        verify(sesionService, times(1)).finalizar(3L);
    }

    @Test
    void debeListarRegistrosDeSesion() {
        when(sesionService.listarRegistros(1L)).thenReturn(List.of());

        List<RegistroResponse> resultado = sesionController.listarRegistros(1L);

        assertTrue(resultado.isEmpty());
        verify(sesionService, times(1)).listarRegistros(1L);
    }

    @Test
    void debeGuardarRegistro() {
        GuardarRegistroRequest req = new GuardarRegistroRequest("Adulto", "30-45", "Solo", false, "Tiendas", "14:00");
        when(sesionService.guardarRegistro(3L, req)).thenReturn(null);

        RegistroResponse resultado = sesionController.guardarRegistro(3L, req);

        assertNull(resultado);
        verify(sesionService, times(1)).guardarRegistro(3L, req);
    }

    @Test
    void debeAnularUltimoRegistro() {
        sesionController.anularUltimoRegistro(3L);

        verify(sesionService, times(1)).anularUltimoRegistro(3L);
    }
}