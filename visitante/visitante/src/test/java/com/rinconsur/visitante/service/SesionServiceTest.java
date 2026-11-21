package com.rinconsur.visitante.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.rinconsur.visitante.client.AccesoClient;
import com.rinconsur.visitante.client.UsuarioClient;
import com.rinconsur.visitante.dto.AccesoResponse;
import com.rinconsur.visitante.dto.CrearSesionRequest;
import com.rinconsur.visitante.dto.EstadisticasResponse;
import com.rinconsur.visitante.dto.GuardarRegistroRequest;
import com.rinconsur.visitante.dto.RegistroResponse;
import com.rinconsur.visitante.dto.SesionResponse;
import com.rinconsur.visitante.dto.UsuarioResponse;
import com.rinconsur.visitante.model.RegistroConteo;
import com.rinconsur.visitante.model.Sesion;
import com.rinconsur.visitante.repository.RegistroConteoRepository;
import com.rinconsur.visitante.repository.SesionRepository;

@ExtendWith(MockitoExtension.class)
class SesionServiceTest {

    @Mock
    private SesionRepository sesionRepository;

    @Mock
    private RegistroConteoRepository registroRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private AccesoClient accesoClient;

    @InjectMocks
    private SesionService sesionService;

    private Sesion sesionFalsa;
    private RegistroConteo registroFalso;

    @BeforeEach
    void setUp() {
        sesionFalsa = Sesion.builder()
                .id(1L)
                .codigo("SES-123")
                .acceso("Puerta Norte")
                .fecha("21-11-2026")
                .estado(Sesion.EN_CURSO)
                .usuarioId(10L)
                .build();

        registroFalso = RegistroConteo.builder()
                .id(100L)
                .sesion(sesionFalsa)
                .clasificacion("Adulto")
                .rangoEtario("30-45")
                .compania("Solo")
                .bolsas(true)
                .destino("Patio Comidas")
                .hora("14:30")
                .build();
    }

    // ==========================================
    // CONSULTAS Y ESTADÍSTICAS
    // ==========================================
    @Test
    void debeListarPorUsuario() {
        when(sesionRepository.findByUsuarioIdOrderByIdDesc(10L)).thenReturn(List.of(sesionFalsa));
        when(registroRepository.countBySesionId(1L)).thenReturn(5L);

        List<SesionResponse> resultado = sesionService.listarPorUsuario(10L);

        assertEquals(1, resultado.size());
        assertEquals("SES-123", resultado.get(0).codigo());
    }

    @Test
    void debeListarActivas() {
        when(sesionRepository.findByEstadoNot(Sesion.FINALIZADA)).thenReturn(List.of(sesionFalsa));
        when(registroRepository.countBySesionId(1L)).thenReturn(2L);

        List<SesionResponse> resultado = sesionService.listarActivas();

        assertEquals(1, resultado.size());
        assertEquals(Sesion.EN_CURSO, resultado.get(0).estado());
    }

    @Test
    void debeObtenerSesionPorIdYFallarSiNoExiste() {
        when(sesionRepository.findById(1L)).thenReturn(Optional.of(sesionFalsa));
        when(registroRepository.countBySesionId(1L)).thenReturn(0L);

        SesionResponse resp = sesionService.obtener(1L);
        assertEquals(1L, resp.id());

        when(sesionRepository.findById(99L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> sesionService.obtener(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void debeCalcularEstadisticas() {
        when(sesionRepository.countByUsuarioId(10L)).thenReturn(8L);
        when(sesionRepository.countByUsuarioIdAndEstado(10L, Sesion.FINALIZADA)).thenReturn(7L);
        when(registroRepository.countBySesionUsuarioId(10L)).thenReturn(150L);

        EstadisticasResponse stats = sesionService.estadisticas(10L);

        assertNotNull(stats);
        verify(sesionRepository, times(1)).countByUsuarioId(10L);
        verify(sesionRepository, times(1)).countByUsuarioIdAndEstado(10L, Sesion.FINALIZADA);
        verify(registroRepository, times(1)).countBySesionUsuarioId(10L);
    }

    // ==========================================
    // CREACIÓN DE SESIÓN Y VALIDACIONES
    // ==========================================
    @Test
    void debeCrearSesionExitosamenteReintentandoCodigoDuplicado() {
        CrearSesionRequest req = new CrearSesionRequest(10L, "  Puerta Norte  ");
        UsuarioResponse observador = mock(UsuarioResponse.class);
        when(observador.rolId()).thenReturn(1L);
        when(observador.activo()).thenReturn(true);

        AccesoResponse puerta = mock(AccesoResponse.class);
        when(puerta.disponible()).thenReturn(true);

        when(usuarioClient.obtener(10L)).thenReturn(observador);
        when(accesoClient.obtener("Puerta Norte")).thenReturn(puerta);
        when(sesionRepository.existsByUsuarioIdAndEstadoNot(10L, Sesion.FINALIZADA)).thenReturn(false);
        when(sesionRepository.existsByAccesoAndEstadoNot("Puerta Norte", Sesion.FINALIZADA)).thenReturn(false);
        when(sesionRepository.existsByCodigo(anyString())).thenReturn(true, false);
        when(sesionRepository.save(any(Sesion.class))).thenReturn(sesionFalsa);

        SesionResponse resp = sesionService.crear(req);

        assertNotNull(resp);
        verify(sesionRepository, times(2)).existsByCodigo(anyString());
        verify(sesionRepository, times(1)).save(any(Sesion.class));
    }

    @Test
    void debeValidarDatosObligatoriosAlCrear() {
        ResponseStatusException ex1 = assertThrows(ResponseStatusException.class,
                () -> sesionService.crear(new CrearSesionRequest(null, "Puerta Norte")));
        ResponseStatusException ex2 = assertThrows(ResponseStatusException.class,
                () -> sesionService.crear(new CrearSesionRequest(10L, null)));
        ResponseStatusException ex3 = assertThrows(ResponseStatusException.class,
                () -> sesionService.crear(new CrearSesionRequest(10L, "   ")));

        assertEquals(HttpStatus.BAD_REQUEST, ex1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, ex2.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, ex3.getStatusCode());
    }

    @Test
    void debeValidarObservadorAlCrear() {
        CrearSesionRequest req = new CrearSesionRequest(10L, "Puerta Norte");
        UsuarioResponse observador = mock(UsuarioResponse.class);
        when(usuarioClient.obtener(10L)).thenReturn(observador);

        // Rol nulo
        when(observador.rolId()).thenReturn(null);
        ResponseStatusException exRolNulo = assertThrows(ResponseStatusException.class, () -> sesionService.crear(req));
        assertEquals(HttpStatus.BAD_REQUEST, exRolNulo.getStatusCode());

        // Rol distinto de 1L (Observador)
        when(observador.rolId()).thenReturn(2L);
        ResponseStatusException exOtroRol = assertThrows(ResponseStatusException.class, () -> sesionService.crear(req));
        assertEquals(HttpStatus.BAD_REQUEST, exOtroRol.getStatusCode());

        // Cuenta suspendida
        when(observador.rolId()).thenReturn(1L);
        when(observador.activo()).thenReturn(false);
        ResponseStatusException exInactivo = assertThrows(ResponseStatusException.class, () -> sesionService.crear(req));
        assertEquals(HttpStatus.FORBIDDEN, exInactivo.getStatusCode());
    }

    @Test
    void debeValidarDisponibilidadDeAccesoYConflictosAlCrear() {
        CrearSesionRequest req = new CrearSesionRequest(10L, "Puerta Norte");
        UsuarioResponse observador = mock(UsuarioResponse.class);
        when(observador.rolId()).thenReturn(1L);
        when(observador.activo()).thenReturn(true);
        when(usuarioClient.obtener(10L)).thenReturn(observador);

        AccesoResponse puerta = mock(AccesoResponse.class);
        when(accesoClient.obtener("Puerta Norte")).thenReturn(puerta);

        // Puerta cerrada
        when(puerta.disponible()).thenReturn(false);
        ResponseStatusException exCerrada = assertThrows(ResponseStatusException.class, () -> sesionService.crear(req));
        assertEquals(HttpStatus.CONFLICT, exCerrada.getStatusCode());

        // Observador ya tiene sesión activa
        when(puerta.disponible()).thenReturn(true);
        when(sesionRepository.existsByUsuarioIdAndEstadoNot(10L, Sesion.FINALIZADA)).thenReturn(true);
        ResponseStatusException exObsActivo = assertThrows(ResponseStatusException.class, () -> sesionService.crear(req));
        assertEquals(HttpStatus.CONFLICT, exObsActivo.getStatusCode());

        // Acceso ya en uso
        when(sesionRepository.existsByUsuarioIdAndEstadoNot(10L, Sesion.FINALIZADA)).thenReturn(false);
        when(sesionRepository.existsByAccesoAndEstadoNot("Puerta Norte", Sesion.FINALIZADA)).thenReturn(true);
        ResponseStatusException exAccesoUso = assertThrows(ResponseStatusException.class, () -> sesionService.crear(req));
        assertEquals(HttpStatus.CONFLICT, exAccesoUso.getStatusCode());
    }

    // ==========================================
    // FINALIZAR SESIÓN Y REGISTROS DE CONTEO
    // ==========================================
    @Test
    void debeFinalizarSesion() {
        when(sesionRepository.findById(1L)).thenReturn(Optional.of(sesionFalsa));
        when(sesionRepository.save(any(Sesion.class))).thenAnswer(inv -> inv.getArgument(0));

        SesionResponse resp = sesionService.finalizar(1L);

        assertEquals(Sesion.FINALIZADA, resp.estado());
        verify(sesionRepository, times(1)).save(sesionFalsa);
    }

    @Test
    void debeListarRegistrosDeSesion() {
        when(sesionRepository.findById(1L)).thenReturn(Optional.of(sesionFalsa));
        when(registroRepository.findBySesionIdOrderByIdAsc(1L)).thenReturn(List.of(registroFalso));

        List<RegistroResponse> registros = sesionService.listarRegistros(1L);

        assertEquals(1, registros.size());
        assertEquals("Adulto", registros.get(0).clasificacion());
    }

    @Test
    void debeGuardarRegistroExitosamente() {
        GuardarRegistroRequest req = new GuardarRegistroRequest("Adulto", "30-45", "Solo", true, "Tiendas", "15:00");
        when(sesionRepository.findById(1L)).thenReturn(Optional.of(sesionFalsa));
        when(registroRepository.save(any(RegistroConteo.class))).thenReturn(registroFalso);

        RegistroResponse resp = sesionService.guardarRegistro(1L, req);

        assertNotNull(resp);
        verify(registroRepository, times(1)).save(any(RegistroConteo.class));
    }

    @Test
    void noDebeGuardarRegistroSiSesionFinalizadaOFaltanDatos() {
        when(sesionRepository.findById(1L)).thenReturn(Optional.of(sesionFalsa));

        // Campos vacíos o nulos
        assertThrows(ResponseStatusException.class,
                () -> sesionService.guardarRegistro(1L, new GuardarRegistroRequest(null, "30-45", "Solo", false, "X", "10:00")));
        assertThrows(ResponseStatusException.class,
                () -> sesionService.guardarRegistro(1L, new GuardarRegistroRequest("Adulto", "", "Solo", false, "X", "10:00")));
        assertThrows(ResponseStatusException.class,
                () -> sesionService.guardarRegistro(1L, new GuardarRegistroRequest("Adulto", "30-45", "  ", false, "X", "10:00")));
        assertThrows(ResponseStatusException.class,
                () -> sesionService.guardarRegistro(1L, new GuardarRegistroRequest("Adulto", "30-45", "Solo", false, "X", null)));

        // Sesión ya finalizada
        sesionFalsa.setEstado(Sesion.FINALIZADA);
        ResponseStatusException exFinalizada = assertThrows(ResponseStatusException.class,
                () -> sesionService.guardarRegistro(1L, new GuardarRegistroRequest("Adulto", "30-45", "Solo", false, "X", "10:00")));
        assertEquals(HttpStatus.CONFLICT, exFinalizada.getStatusCode());
    }

    @Test
    void debeAnularUltimoRegistroYFallarSiNoHayRegistros() {
        when(sesionRepository.findById(1L)).thenReturn(Optional.of(sesionFalsa));
        when(registroRepository.findFirstBySesionIdOrderByIdDesc(1L)).thenReturn(Optional.of(registroFalso));

        sesionService.anularUltimoRegistro(1L);
        verify(registroRepository, times(1)).delete(registroFalso);

        when(registroRepository.findFirstBySesionIdOrderByIdDesc(1L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> sesionService.anularUltimoRegistro(1L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}