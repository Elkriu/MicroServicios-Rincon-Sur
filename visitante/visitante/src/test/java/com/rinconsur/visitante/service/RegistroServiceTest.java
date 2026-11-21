package com.rinconsur.visitante.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.rinconsur.visitante.dto.GuardarRegistroRequest;
import com.rinconsur.visitante.dto.RegistroDetalleResponse;
import com.rinconsur.visitante.model.RegistroConteo;
import com.rinconsur.visitante.model.Sesion;
import com.rinconsur.visitante.repository.RegistroConteoRepository;

@ExtendWith(MockitoExtension.class)
class RegistroServiceTest {

    @Mock
    private RegistroConteoRepository registroRepository;

    @InjectMocks
    private RegistroService registroService;

    private Sesion sesionFalsa;
    private RegistroConteo registroFalso;

    @BeforeEach
    void setUp() {
        sesionFalsa = Sesion.builder()
                .id(1L)
                .codigo("SES-101")
                .acceso("Puerta Sur")
                .fecha("21-11-2026")
                .estado(Sesion.EN_CURSO)
                .usuarioId(10L)
                .build();

        registroFalso = RegistroConteo.builder()
                .id(50L)
                .sesion(sesionFalsa)
                .clasificacion("Adulto")
                .rangoEtario("30-45")
                .compania("Solo")
                .bolsas(false)
                .destino("Supermercado")
                .hora("10:15")
                .build();
    }

    @Test
    void debeListarRegistrosNormalizandoFiltros() {
        when(registroRepository.buscar("21-11-2026", "Puerta Sur")).thenReturn(List.of(registroFalso));
        when(registroRepository.buscar(null, null)).thenReturn(List.of());

        List<RegistroDetalleResponse> conFiltros = registroService.listar("  21-11-2026  ", "  Puerta Sur  ");
        List<RegistroDetalleResponse> sinFiltros = registroService.listar("   ", null);

        assertEquals(1, conFiltros.size());
        assertEquals(50L, conFiltros.get(0).id());
        assertEquals(1L, conFiltros.get(0).sesionId());
        assertTrue(sinFiltros.isEmpty());
    }

    @Test
    void debeListarFechasOrdenadasDeMasRecienteAMasAntigua() {
        when(registroRepository.fechasConRegistros())
                .thenReturn(List.of("15-10-2026", "21-11-2026", "01-01-2026"));

        List<String> fechas = registroService.fechas();

        assertEquals(List.of("21-11-2026", "15-10-2026", "01-01-2026"), fechas);
    }

    @Test
    void debeEditarRegistroConYSinHoraNueva() {
        when(registroRepository.findById(50L)).thenReturn(Optional.of(registroFalso));
        when(registroRepository.save(any(RegistroConteo.class))).thenAnswer(inv -> inv.getArgument(0));

        // Con hora nueva y espacios extra
        GuardarRegistroRequest reqConHora = new GuardarRegistroRequest(
                "  Adulto Mayor  ", "  60+  ", "  Pareja  ", true, "  Farmacia  ", "  11:45  "
        );
        RegistroDetalleResponse resp1 = registroService.editar(50L, reqConHora);

        assertEquals("Adulto Mayor", resp1.clasificacion());
        assertEquals("60+", resp1.rangoEtario());
        assertEquals("Pareja", resp1.compania());
        assertTrue(resp1.bolsas());
        assertEquals("Farmacia", resp1.destino());
        assertEquals("11:45", resp1.hora());

        // Sin hora nueva (mantiene "11:45") y destino en blanco (se normaliza a null)
        GuardarRegistroRequest reqSinHora = new GuardarRegistroRequest(
                "Joven", "18-29", "Grupo", false, "   ", "   "
        );
        RegistroDetalleResponse resp2 = registroService.editar(50L, reqSinHora);

        assertEquals("11:45", resp2.hora());
        assertNull(resp2.destino());
    }

    @Test
    void debeFallarAlEditarSiNoExisteOFaltanCamposObligatorios() {
        when(registroRepository.findById(99L)).thenReturn(Optional.empty());
        GuardarRegistroRequest reqValido = new GuardarRegistroRequest("Adulto", "30-45", "Solo", false, null, null);

        ResponseStatusException exNoExiste = assertThrows(ResponseStatusException.class,
                () -> registroService.editar(99L, reqValido));
        assertEquals(HttpStatus.NOT_FOUND, exNoExiste.getStatusCode());

        when(registroRepository.findById(50L)).thenReturn(Optional.of(registroFalso));

        assertThrows(ResponseStatusException.class,
                () -> registroService.editar(50L, new GuardarRegistroRequest(null, "30-45", "Solo", false, null, null)));
        assertThrows(ResponseStatusException.class,
                () -> registroService.editar(50L, new GuardarRegistroRequest("Adulto", "   ", "Solo", false, null, null)));
        ResponseStatusException exBadRequest = assertThrows(ResponseStatusException.class,
                () -> registroService.editar(50L, new GuardarRegistroRequest("Adulto", "30-45", "", false, null, null)));
        assertEquals(HttpStatus.BAD_REQUEST, exBadRequest.getStatusCode());
    }

    @Test
    void debeEliminarRegistroYFallarSiNoExiste() {
        when(registroRepository.existsById(50L)).thenReturn(true);

        registroService.eliminar(50L);
        verify(registroRepository, times(1)).deleteById(50L);

        when(registroRepository.existsById(99L)).thenReturn(false);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> registroService.eliminar(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}