package com.rinconsur.administracion.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rinconsur.administracion.dto.AccesoResponse;
import com.rinconsur.administracion.dto.CambiarDisponibilidadRequest;
import com.rinconsur.administracion.service.AccesoService;

@ExtendWith(MockitoExtension.class)
class AccesoControllerTest {

    @Mock
    private AccesoService accesoService;

    @InjectMocks
    private AccesoController accesoController;

    @Test
    void debeListarAccesos() {
        List<AccesoResponse> lista = List.of(new AccesoResponse("Acceso Norte", true));
        when(accesoService.listar(true)).thenReturn(lista);

        List<AccesoResponse> resultado = accesoController.listar(true);

        assertEquals(1, resultado.size());
        assertEquals("Acceso Norte", resultado.get(0).nombre());
        verify(accesoService, times(1)).listar(true);
    }

    @Test
    void debeObtenerAccesoPorNombre() {
        AccesoResponse esperado = new AccesoResponse("Acceso Norte", true);
        when(accesoService.obtener("Acceso Norte")).thenReturn(esperado);

        AccesoResponse resultado = accesoController.obtener("Acceso Norte");

        assertEquals(esperado, resultado);
        verify(accesoService, times(1)).obtener("Acceso Norte");
    }

    @Test
    void debeCambiarDisponibilidad() {
        CambiarDisponibilidadRequest req = new CambiarDisponibilidadRequest(false);
        AccesoResponse esperado = new AccesoResponse("Acceso Norte", false);
        when(accesoService.cambiarDisponibilidad("Acceso Norte", req)).thenReturn(esperado);

        AccesoResponse resultado = accesoController.cambiarDisponibilidad("Acceso Norte", req);

        assertFalse(resultado.disponible());
        verify(accesoService, times(1)).cambiarDisponibilidad("Acceso Norte", req);
    }
}