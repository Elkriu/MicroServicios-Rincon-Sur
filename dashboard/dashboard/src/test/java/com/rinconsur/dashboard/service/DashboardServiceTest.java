package com.rinconsur.dashboard.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.rinconsur.dashboard.client.VisitanteClient;
import com.rinconsur.dashboard.dto.DistribucionResponse;
import com.rinconsur.dashboard.dto.RegistroDetalleResponse;
import com.rinconsur.dashboard.dto.ResumenDiarioResponse;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private VisitanteClient visitanteClient;

    @InjectMocks
    private DashboardService dashboardService;

    private List<RegistroDetalleResponse> registrosFalsos;

    @BeforeEach
    void setUp() {
        registrosFalsos = List.of(
                new RegistroDetalleResponse(1L, 10L, "Puerta Norte", "21-11-2026", "10:00",
                        "Adulto", "30-45", "Solo", true, "Supermercado"),
                new RegistroDetalleResponse(2L, 10L, "Puerta Norte", "21-11-2026", "10:15",
                        "Joven", "18-29", "Pareja", false, "Tiendas"),
                new RegistroDetalleResponse(3L, 20L, "Puerta Sur", "21-11-2026", "11:00",
                        "Adulto", "30-45", "Solo", false, "Farmacia")
        );
    }

    @Test
    void debeListarFechas() {
        List<String> fechas = List.of("21-11-2026", "20-11-2026");
        when(visitanteClient.listarFechas()).thenReturn(fechas);

        assertEquals(fechas, dashboardService.fechas());
        verify(visitanteClient, times(1)).listarFechas();
    }

    @Test
    void debeGenerarResumenDiario() {
        when(visitanteClient.listarRegistros("21-11-2026", null)).thenReturn(registrosFalsos);

        ResumenDiarioResponse resumen = dashboardService.resumen("21-11-2026");

        assertEquals("21-11-2026", resumen.fecha());
        assertEquals(3L, resumen.totalMuestras());
        assertEquals(2L, resumen.sesiones());
        assertEquals(2, resumen.flujoPorAcceso().size());
        assertEquals("Puerta Norte", resumen.flujoPorAcceso().get(0).etiqueta());
        assertEquals(2L, resumen.flujoPorAcceso().get(0).cantidad());
        assertEquals("Puerta Sur", resumen.flujoPorAcceso().get(1).etiqueta());
        assertEquals(1L, resumen.flujoPorAcceso().get(1).cantidad());
    }

    @Test
    void debeValidarFechaObligatoriaYFormatoEnResumen() {
        ResponseStatusException exNull = assertThrows(ResponseStatusException.class,
                () -> dashboardService.resumen(null));
        assertEquals(HttpStatus.BAD_REQUEST, exNull.getStatusCode());

        ResponseStatusException exBlank = assertThrows(ResponseStatusException.class,
                () -> dashboardService.resumen("   "));
        assertEquals(HttpStatus.BAD_REQUEST, exBlank.getStatusCode());

        ResponseStatusException exFormato = assertThrows(ResponseStatusException.class,
                () -> dashboardService.resumen("2026-11-21"));
        assertEquals(HttpStatus.BAD_REQUEST, exFormato.getStatusCode());
    }

    @Test
    void debeGenerarDistribucionConYSinFiltros() {
        when(visitanteClient.listarRegistros("21-11-2026", "Puerta Norte")).thenReturn(registrosFalsos);
        when(visitanteClient.listarRegistros(null, null)).thenReturn(List.of());

        DistribucionResponse distConFiltros = dashboardService.distribucion("  21-11-2026  ", "  Puerta Norte  ");
        assertEquals("21-11-2026", distConFiltros.fecha());
        assertEquals("Puerta Norte", distConFiltros.acceso());
        assertEquals(3, distConFiltros.total());
        assertEquals("Adulto", distConFiltros.porClasificacion().get(0).etiqueta());
        assertEquals(2L, distConFiltros.porClasificacion().get(0).cantidad());
        assertEquals("No trae", distConFiltros.porBolsas().get(0).etiqueta());
        assertEquals(2L, distConFiltros.porBolsas().get(0).cantidad());
        assertEquals("Sí trae", distConFiltros.porBolsas().get(1).etiqueta());
        assertEquals(1L, distConFiltros.porBolsas().get(1).cantidad());

        DistribucionResponse distSinFiltros = dashboardService.distribucion(null, "   ");
        assertNull(distSinFiltros.fecha());
        assertNull(distSinFiltros.acceso());
        assertEquals(0, distSinFiltros.total());
    }

    @Test
    void debeListarRegistrosConYSinFiltrosYValidarFormatoFecha() {
        when(visitanteClient.listarRegistros("21-11-2026", "Puerta Sur")).thenReturn(registrosFalsos);
        when(visitanteClient.listarRegistros(null, null)).thenReturn(List.of());

        List<RegistroDetalleResponse> res1 = dashboardService.registros(" 21-11-2026 ", " Puerta Sur ");
        assertEquals(3, res1.size());

        List<RegistroDetalleResponse> res2 = dashboardService.registros("  ", null);
        assertTrue(res2.isEmpty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> dashboardService.registros("fecha-mala", "Puerta Norte"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}