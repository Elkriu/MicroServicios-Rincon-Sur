package com.rinconsur.usuario.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import com.rinconsur.usuario.dto.CodigoResponse;
import com.rinconsur.usuario.dto.RestablecerContrasenaRequest;
import com.rinconsur.usuario.dto.SolicitarCodigoRequest;
import com.rinconsur.usuario.dto.VerificarCodigoRequest;
import com.rinconsur.usuario.model.TokenRecuperacion;
import com.rinconsur.usuario.model.Usuario;
import com.rinconsur.usuario.repository.TokenRecuperacionRepository;

@ExtendWith(MockitoExtension.class)
class RecuperacionServiceTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private TokenRecuperacionRepository tokenRepository;

    @InjectMocks
    private RecuperacionService recuperacionService;

    private Usuario usuarioFalso;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recuperacionService, "minutosVigencia", 15L);
        ReflectionTestUtils.setField(recuperacionService, "devolverCodigo", true);

        usuarioFalso = new Usuario();
        usuarioFalso.setId(1L);
        usuarioFalso.setCorreo("juan@rinconsur.cl");
        usuarioFalso.setActivo(true);
    }

    // ==========================================
    // TESTS PARA SOLICITAR CÓDIGO
    // ==========================================
    @Test
    void debeSolicitarCodigoYDevolverloCuandoEstaHabilitado() {
        SolicitarCodigoRequest request = new SolicitarCodigoRequest("  JUAN@rinconsur.cl ");
        when(usuarioService.normalizarCorreo(request.correo())).thenReturn("juan@rinconsur.cl");
        when(usuarioService.buscarPorCorreo("juan@rinconsur.cl")).thenReturn(usuarioFalso);

        CodigoResponse response = recuperacionService.solicitar(request);

        assertNotNull(response);
        assertNotNull(response.codigo());
        assertEquals(6, response.codigo().length());
        assertTrue(response.mensaje().contains("15 minutos"));
        verify(tokenRepository, times(1)).save(any(TokenRecuperacion.class));
    }

    @Test
    void debeSolicitarCodigoSinDevolverloCuandoEstaDeshabilitado() {
        ReflectionTestUtils.setField(recuperacionService, "devolverCodigo", false);
        SolicitarCodigoRequest request = new SolicitarCodigoRequest("juan@rinconsur.cl");
        when(usuarioService.normalizarCorreo(anyString())).thenReturn("juan@rinconsur.cl");
        when(usuarioService.buscarPorCorreo("juan@rinconsur.cl")).thenReturn(usuarioFalso);

        CodigoResponse response = recuperacionService.solicitar(request);

        assertNotNull(response);
        assertNull(response.codigo());
        verify(tokenRepository, times(1)).save(any(TokenRecuperacion.class));
    }

    @Test
    void noDebeSolicitarCodigoSiCuentaEstaSuspendida() {
        usuarioFalso.setActivo(false);
        SolicitarCodigoRequest request = new SolicitarCodigoRequest("juan@rinconsur.cl");
        when(usuarioService.normalizarCorreo(anyString())).thenReturn("juan@rinconsur.cl");
        when(usuarioService.buscarPorCorreo("juan@rinconsur.cl")).thenReturn(usuarioFalso);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> recuperacionService.solicitar(request));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(tokenRepository, never()).save(any());
    }

    // ==========================================
    // TESTS PARA VERIFICAR CÓDIGO
    // ==========================================
    @Test
    void debeVerificarCodigoValido() {
        VerificarCodigoRequest request = new VerificarCodigoRequest("juan@rinconsur.cl", "123456");
        TokenRecuperacion token = TokenRecuperacion.builder()
                .correo("juan@rinconsur.cl")
                .codigo("123456")
                .expira(LocalDateTime.now().plusMinutes(10))
                .build();

        when(usuarioService.normalizarCorreo(anyString())).thenReturn("juan@rinconsur.cl");
        when(tokenRepository.findById("juan@rinconsur.cl")).thenReturn(Optional.of(token));

        assertDoesNotThrow(() -> recuperacionService.verificar(request));
    }

    @Test
    void debeFallarVerificacionSiCodigoNoTiene6Digitos() {
        VerificarCodigoRequest requestCorto = new VerificarCodigoRequest("juan@rinconsur.cl", "123");
        VerificarCodigoRequest requestNulo = new VerificarCodigoRequest("juan@rinconsur.cl", null);
        when(usuarioService.normalizarCorreo(anyString())).thenReturn("juan@rinconsur.cl");

        ResponseStatusException ex1 = assertThrows(ResponseStatusException.class,
                () -> recuperacionService.verificar(requestCorto));
        ResponseStatusException ex2 = assertThrows(ResponseStatusException.class,
                () -> recuperacionService.verificar(requestNulo));

        assertEquals(HttpStatus.BAD_REQUEST, ex1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, ex2.getStatusCode());
    }

    @Test
    void debeFallarVerificacionSiCodigoEsIncorrecto() {
        VerificarCodigoRequest request = new VerificarCodigoRequest("juan@rinconsur.cl", "999999");
        TokenRecuperacion token = TokenRecuperacion.builder()
                .correo("juan@rinconsur.cl")
                .codigo("123456")
                .expira(LocalDateTime.now().plusMinutes(10))
                .build();

        when(usuarioService.normalizarCorreo(anyString())).thenReturn("juan@rinconsur.cl");
        when(tokenRepository.findById("juan@rinconsur.cl")).thenReturn(Optional.of(token));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> recuperacionService.verificar(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("incorrecto"));
    }

    @Test
    void debeFallarVerificacionYEliminarSiCodigoVencio() {
        VerificarCodigoRequest request = new VerificarCodigoRequest("juan@rinconsur.cl", "123456");
        TokenRecuperacion tokenVencido = TokenRecuperacion.builder()
                .correo("juan@rinconsur.cl")
                .codigo("123456")
                .expira(LocalDateTime.now().minusMinutes(1))
                .build();

        when(usuarioService.normalizarCorreo(anyString())).thenReturn("juan@rinconsur.cl");
        when(tokenRepository.findById("juan@rinconsur.cl")).thenReturn(Optional.of(tokenVencido));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> recuperacionService.verificar(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("venció"));
        verify(tokenRepository, times(1)).delete(tokenVencido);
    }

    // ==========================================
    // TESTS PARA RESTABLECER CONTRASEÑA
    // ==========================================
    @Test
    void debeRestablecerContrasenaExitosamente() {
        RestablecerContrasenaRequest request = new RestablecerContrasenaRequest("juan@rinconsur.cl", "123456", "NuevaClave1!");
        TokenRecuperacion token = TokenRecuperacion.builder()
                .correo("juan@rinconsur.cl")
                .codigo("123456")
                .expira(LocalDateTime.now().plusMinutes(10))
                .build();

        when(usuarioService.normalizarCorreo(anyString())).thenReturn("juan@rinconsur.cl");
        when(tokenRepository.findById("juan@rinconsur.cl")).thenReturn(Optional.of(token));
        when(usuarioService.buscarPorCorreo("juan@rinconsur.cl")).thenReturn(usuarioFalso);

        recuperacionService.restablecer(request);

        verify(usuarioService, times(1)).validarClave("NuevaClave1!");
        verify(usuarioService, times(1)).guardarContrasena(usuarioFalso, "NuevaClave1!");
        verify(tokenRepository, times(1)).deleteById("juan@rinconsur.cl");
    }

    @Test
    void noDebeRestablecerSiCuentaEstaSuspendida() {
        usuarioFalso.setActivo(false);
        RestablecerContrasenaRequest request = new RestablecerContrasenaRequest("juan@rinconsur.cl", "123456", "NuevaClave1!");
        TokenRecuperacion token = TokenRecuperacion.builder()
                .correo("juan@rinconsur.cl")
                .codigo("123456")
                .expira(LocalDateTime.now().plusMinutes(10))
                .build();

        when(usuarioService.normalizarCorreo(anyString())).thenReturn("juan@rinconsur.cl");
        when(tokenRepository.findById("juan@rinconsur.cl")).thenReturn(Optional.of(token));
        when(usuarioService.buscarPorCorreo("juan@rinconsur.cl")).thenReturn(usuarioFalso);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> recuperacionService.restablecer(request));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(usuarioService, never()).guardarContrasena(any(), anyString());
    }
}