package com.rinconsur.visitante.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.rinconsur.visitante.dto.GuardarRegistroRequest;
import com.rinconsur.visitante.dto.RegistroDetalleResponse;
import com.rinconsur.visitante.service.RegistroService;

@ExtendWith(MockitoExtension.class)
class RegistroControllerTest {

    @Mock
    private RegistroService registroService;

    @InjectMocks
    private RegistroController registroController;

    @Test
    void debeListarRegistros() {
        when(registroService.listar("01-11-2026", "Acceso A1")).thenReturn(List.of());

        List<RegistroDetalleResponse> resultado = registroController.listar("01-11-2026", "Acceso A1");

        assertTrue(resultado.isEmpty());
        verify(registroService, times(1)).listar("01-11-2026", "Acceso A1");
    }

    @Test
    void debeListarFechas() {
        List<String> fechasEsperadas = List.of("21-11-2026", "01-11-2026");
        when(registroService.fechas()).thenReturn(fechasEsperadas);

        List<String> resultado = registroController.fechas();

        assertEquals(fechasEsperadas, resultado);
        verify(registroService, times(1)).fechas();
    }

    @Test
    void debeEditarRegistro() {
        GuardarRegistroRequest req = new GuardarRegistroRequest("Adulto", "30-45", "Solo", false, "Tiendas", "12:00");
        when(registroService.editar(1L, req)).thenReturn(null);

        RegistroDetalleResponse resultado = registroController.editar(1L, req);

        assertNull(resultado);
        verify(registroService, times(1)).editar(1L, req);
    }

    @Test
    void debeEliminarRegistro() {
        ResponseEntity<Void> respuesta = registroController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(registroService, times(1)).eliminar(1L);
    }
}