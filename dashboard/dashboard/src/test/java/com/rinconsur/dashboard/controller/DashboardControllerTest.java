package com.rinconsur.dashboard.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rinconsur.dashboard.dto.DistribucionResponse;
import com.rinconsur.dashboard.dto.RegistroDetalleResponse;
import com.rinconsur.dashboard.dto.ResumenDiarioResponse;
import com.rinconsur.dashboard.service.DashboardService;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    @Test
    void debeListarFechas() {
        List<String> fechas = List.of("21-11-2026", "20-11-2026");
        when(dashboardService.fechas()).thenReturn(fechas);

        List<String> resultado = dashboardController.fechas();

        assertEquals(fechas, resultado);
        verify(dashboardService, times(1)).fechas();
    }

    @Test
    void debeObtenerResumen() {
        ResumenDiarioResponse esperado = new ResumenDiarioResponse("21-11-2026", 10L, 4L, List.of());
        when(dashboardService.resumen("21-11-2026")).thenReturn(esperado);

        ResumenDiarioResponse resultado = dashboardController.resumen("21-11-2026");

        assertEquals(esperado, resultado);
        verify(dashboardService, times(1)).resumen("21-11-2026");
    }

    @Test
    void debeObtenerDistribucion() {
        DistribucionResponse esperado = new DistribucionResponse(
                "21-11-2026", "Puerta Norte", 5L, List.of(), List.of(), List.of(), List.of());
        when(dashboardService.distribucion("21-11-2026", "Puerta Norte")).thenReturn(esperado);

        DistribucionResponse resultado = dashboardController.distribucion("21-11-2026", "Puerta Norte");

        assertEquals(esperado, resultado);
        verify(dashboardService, times(1)).distribucion("21-11-2026", "Puerta Norte");
    }

    @Test
    void debeListarRegistros() {
        List<RegistroDetalleResponse> esperado = List.of(
                new RegistroDetalleResponse(1L, 10L, "Puerta Norte", "21-11-2026", "10:00",
                        "Adulto", "30-45", "Solo", true, "Supermercado")
        );
        when(dashboardService.registros("21-11-2026", "Puerta Norte")).thenReturn(esperado);

        List<RegistroDetalleResponse> resultado = dashboardController.registros("21-11-2026", "Puerta Norte");

        assertEquals(1, resultado.size());
        assertEquals(esperado, resultado);
        verify(dashboardService, times(1)).registros("21-11-2026", "Puerta Norte");
    }
}