package com.rinconsur.usuario.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.rinconsur.usuario.dto.ActualizarPerfilRequest;
import com.rinconsur.usuario.dto.CambiarContrasenaRequest;
import com.rinconsur.usuario.dto.CambiarEstadoRequest;
import com.rinconsur.usuario.dto.CambiarRolRequest;
import com.rinconsur.usuario.dto.CrearUsuarioRequest;
import com.rinconsur.usuario.dto.LoginRequest;
import com.rinconsur.usuario.dto.UsuarioResponse;
import com.rinconsur.usuario.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    void debeListarUsuarios() {
        when(usuarioService.listar("juan", 1L, true)).thenReturn(List.of());

        List<UsuarioResponse> resultado = usuarioController.listar("juan", 1L, true);

        assertTrue(resultado.isEmpty());
        verify(usuarioService, times(1)).listar("juan", 1L, true);
    }

    @Test
    void debeObtenerUsuarioPorId() {
        when(usuarioService.obtener(1L)).thenReturn(null);

        UsuarioResponse resultado = usuarioController.obtener(1L);

        assertNull(resultado);
        verify(usuarioService, times(1)).obtener(1L);
    }

    @Test
    void debeCrearUsuario() {
        CrearUsuarioRequest req = new CrearUsuarioRequest("Juan Perez", "juan@rinconsur.cl", "Segura123!", 1L);
        when(usuarioService.crear(req)).thenReturn(null);

        UsuarioResponse resultado = usuarioController.crear(req);

        assertNull(resultado);
        verify(usuarioService, times(1)).crear(req);
    }

    @Test
    void debeIniciarSesion() {
        LoginRequest req = new LoginRequest("juan@rinconsur.cl", "Segura123!");
        when(usuarioService.iniciarSesion(req)).thenReturn(null);

        UsuarioResponse resultado = usuarioController.iniciarSesion(req);

        assertNull(resultado);
        verify(usuarioService, times(1)).iniciarSesion(req);
    }

    @Test
    void debeActualizarPerfil() {
        ActualizarPerfilRequest req = new ActualizarPerfilRequest("Juan Perez", "juan@rinconsur.cl");
        when(usuarioService.actualizarPerfil(1L, req)).thenReturn(null);

        UsuarioResponse resultado = usuarioController.actualizarPerfil(1L, req);

        assertNull(resultado);
        verify(usuarioService, times(1)).actualizarPerfil(1L, req);
    }

    @Test
    void debeSubirFoto() {
        MultipartFile archivo = mock(MultipartFile.class);
        when(usuarioService.subirFoto(1L, archivo)).thenReturn(null);

        UsuarioResponse resultado = usuarioController.subirFoto(1L, archivo);

        assertNull(resultado);
        verify(usuarioService, times(1)).subirFoto(1L, archivo);
    }

    @Test
    void debeObtenerFoto() {
        byte[] bytesFoto = new byte[]{10, 20, 30};
        when(usuarioService.obtenerFoto(1L)).thenReturn(bytesFoto);

        ResponseEntity<byte[]> respuesta = usuarioController.obtenerFoto(1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, respuesta.getHeaders().getContentType());
        assertArrayEquals(bytesFoto, respuesta.getBody());
        verify(usuarioService, times(1)).obtenerFoto(1L);
    }

    @Test
    void debeCambiarContrasena() {
        CambiarContrasenaRequest req = new CambiarContrasenaRequest("vieja123", "Nueva123!");

        usuarioController.cambiarContrasena(1L, req);

        verify(usuarioService, times(1)).cambiarContrasena(1L, req);
    }

    @Test
    void debeCambiarRol() {
        CambiarRolRequest req = new CambiarRolRequest(1L);
        when(usuarioService.cambiarRol(1L, req)).thenReturn(null);

        UsuarioResponse resultado = usuarioController.cambiarRol(1L, req);

        assertNull(resultado);
        verify(usuarioService, times(1)).cambiarRol(1L, req);
    }

    @Test
    void debeCambiarEstado() {
        CambiarEstadoRequest req = new CambiarEstadoRequest(true);
        when(usuarioService.cambiarEstado(1L, req)).thenReturn(null);

        UsuarioResponse resultado = usuarioController.cambiarEstado(1L, req);

        assertNull(resultado);
        verify(usuarioService, times(1)).cambiarEstado(1L, req);
    }
}