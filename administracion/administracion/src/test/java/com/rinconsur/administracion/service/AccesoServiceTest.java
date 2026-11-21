package com.rinconsur.administracion.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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

import com.rinconsur.administracion.dto.AccesoResponse;
import com.rinconsur.administracion.dto.CambiarDisponibilidadRequest;
import com.rinconsur.administracion.model.Acceso;
import com.rinconsur.administracion.repository.AccesoRepository;

@ExtendWith(MockitoExtension.class)
class AccesoServiceTest {

    @Mock
    private AccesoRepository accesoRepository;

    @InjectMocks
    private AccesoService accesoService;

    private Acceso accesoNorte;

    @BeforeEach
    void setUp() {
        accesoNorte = Acceso.builder()
                .nombre("Acceso Norte")
                .disponible(true)
                .build();
    }

    @Test
    void debeListarTodosCuandoFiltroEsNulo() {
        when(accesoRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(accesoNorte));

        List<AccesoResponse> resultado = accesoService.listar(null);

        assertEquals(1, resultado.size());
        assertEquals("Acceso Norte", resultado.get(0).nombre());
        assertTrue(resultado.get(0).disponible());
        verify(accesoRepository, times(1)).findAllByOrderByNombreAsc();
        verify(accesoRepository, never()).findByDisponibleOrderByNombreAsc(anyBoolean());
    }
    
    @Test
    void debeListarFiltrandoPorDisponibilidad() {
        when(accesoRepository.findByDisponibleOrderByNombreAsc(true)).thenReturn(List.of(accesoNorte));

        List<AccesoResponse> resultado = accesoService.listar(true);

        assertEquals(1, resultado.size());
        assertEquals("Acceso Norte", resultado.get(0).nombre());
        verify(accesoRepository, times(1)).findByDisponibleOrderByNombreAsc(true);
        verify(accesoRepository, never()).findAllByOrderByNombreAsc();
    }

    @Test
    void debeObtenerAccesoYFallarSiNoExiste() {
        when(accesoRepository.findById("Acceso Norte")).thenReturn(Optional.of(accesoNorte));

        AccesoResponse resp = accesoService.obtener("Acceso Norte");
        assertEquals("Acceso Norte", resp.nombre());
        assertTrue(resp.disponible());

        when(accesoRepository.findById("Inexistente")).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> accesoService.obtener("Inexistente"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void debeCambiarDisponibilidadExitosamente() {
        when(accesoRepository.findById("Acceso Norte")).thenReturn(Optional.of(accesoNorte));
        when(accesoRepository.save(any(Acceso.class))).thenAnswer(inv -> inv.getArgument(0));

        AccesoResponse resp = accesoService.cambiarDisponibilidad("Acceso Norte", new CambiarDisponibilidadRequest(false));

        assertEquals("Acceso Norte", resp.nombre());
        assertFalse(resp.disponible());
        verify(accesoRepository, times(1)).save(accesoNorte);
    }

    @Test
    void debeFallarAlCambiarDisponibilidadSiDatoEsNuloOAccesoNoExiste() {
        ResponseStatusException exNulo = assertThrows(ResponseStatusException.class,
                () -> accesoService.cambiarDisponibilidad("Acceso Norte", new CambiarDisponibilidadRequest(null)));
        assertEquals(HttpStatus.BAD_REQUEST, exNulo.getStatusCode());

        when(accesoRepository.findById("Inexistente")).thenReturn(Optional.empty());
        ResponseStatusException exNoExiste = assertThrows(ResponseStatusException.class,
                () -> accesoService.cambiarDisponibilidad("Inexistente", new CambiarDisponibilidadRequest(true)));
        assertEquals(HttpStatus.NOT_FOUND, exNoExiste.getStatusCode());
    }
}