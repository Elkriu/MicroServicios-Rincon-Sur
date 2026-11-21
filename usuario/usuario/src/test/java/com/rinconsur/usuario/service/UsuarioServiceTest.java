package com.rinconsur.usuario.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.rinconsur.usuario.dto.ActualizarPerfilRequest;
import com.rinconsur.usuario.dto.CambiarContrasenaRequest;
import com.rinconsur.usuario.dto.CambiarEstadoRequest;
import com.rinconsur.usuario.dto.CambiarRolRequest;
import com.rinconsur.usuario.dto.CrearUsuarioRequest;
import com.rinconsur.usuario.dto.LoginRequest;
import com.rinconsur.usuario.dto.RolResponse;
import com.rinconsur.usuario.dto.UsuarioResponse;
import com.rinconsur.usuario.model.Rol;
import com.rinconsur.usuario.model.Usuario;
import com.rinconsur.usuario.repository.RolRepository;
import com.rinconsur.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AlmacenamientoService almacenamientoService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioFalso;
    private Rol rolFalso;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        rolFalso = new Rol();
        rolFalso.setId(1L);
        rolFalso.setNombre("ADMIN");

        usuarioFalso = new Usuario();
        usuarioFalso.setId(1L);
        usuarioFalso.setNombre("Juan Perez");
        usuarioFalso.setCorreo("juan.perez@rinconsur.cl");
        usuarioFalso.setContrasena("hash123");
        usuarioFalso.setActivo(true);
        usuarioFalso.setRol(rolFalso);
    }

    // ==========================================
    // CONSULTAS Y LISTADOS
    // ==========================================
    @Test
    void debeListarRoles() {
        when(rolRepository.findAll()).thenReturn(List.of(rolFalso));

        List<RolResponse> roles = usuarioService.listarRoles();

        assertFalse(roles.isEmpty());
        assertEquals(1L, roles.get(0).id());
        assertEquals("ADMIN", roles.get(0).nombre());
    }

    @Test
    void debeListarUsuariosSinFiltros() {
        when(usuarioRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(usuarioFalso));

        List<UsuarioResponse> usuarios = usuarioService.listar(null, null, null);
        assertEquals(1, usuarios.size());
    }

    @Test
    void debeListarUsuariosConBusquedaEnBlanco() {
        when(usuarioRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(usuarioFalso));

        List<UsuarioResponse> usuarios = usuarioService.listar("   ", null, null);
        assertEquals(1, usuarios.size());
    }

    @Test
    void debeListarUsuariosConFiltrosYDescartarNoCoincidentes() {
        when(usuarioRepository.findByNombreContainingIgnoreCaseOrCorreoContainingIgnoreCaseOrderByNombreAsc("juan", "juan"))
                .thenReturn(List.of(usuarioFalso));

        assertEquals(1, usuarioService.listar("juan", 1L, true).size());
        assertTrue(usuarioService.listar("juan", 99L, true).isEmpty());
        assertTrue(usuarioService.listar("juan", 1L, false).isEmpty());
    }

    @Test
    void debeObtenerUsuarioPorId() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioFalso));

        UsuarioResponse response = usuarioService.obtener(1L);
        assertEquals("Juan Perez", response.nombre());
    }

    @Test
    void debeLanzarNotFoundSiUsuarioNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> usuarioService.obtener(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    // ==========================================
    // CREACIÓN DE USUARIOS Y VALIDACIONES
    // ==========================================
    @Test
    void debeCrearUsuarioConRolPorDefectoCuandoRolIdEsNulo() {
        CrearUsuarioRequest request = new CrearUsuarioRequest("Pedro Pablo", "pedro.pablo@rinconsur.cl", "Segura123!", null);

        when(usuarioRepository.existsByCorreo(anyString())).thenReturn(false);
        when(rolRepository.findById(Rol.ID_OBSERVADOR)).thenReturn(Optional.of(rolFalso));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioFalso);

        UsuarioResponse response = usuarioService.crear(request);
        assertNotNull(response);
    }

    @Test
    void noDebeCrearUsuarioSiRolNoExiste() {
        CrearUsuarioRequest request = new CrearUsuarioRequest("Pedro Pablo", "pedro.pablo@rinconsur.cl", "Segura123!", 99L);

        when(usuarioRepository.existsByCorreo(anyString())).thenReturn(false);
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> usuarioService.crear(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void noDebeCrearUsuarioSiCorreoYaExiste() {
        CrearUsuarioRequest request = new CrearUsuarioRequest("Pedro Pablo", "pedro@rinconsur.cl", "Segura123!", 1L);
        when(usuarioRepository.existsByCorreo(anyString())).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> usuarioService.crear(request));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void debeValidarNombreCorrectamente() {
        assertThrows(ResponseStatusException.class,
                () -> usuarioService.crear(new CrearUsuarioRequest(null, "a@rinconsur.cl", "Segura123!", 1L)));
        assertThrows(ResponseStatusException.class,
                () -> usuarioService.crear(new CrearUsuarioRequest("   ", "a@rinconsur.cl", "Segura123!", 1L)));
        assertThrows(ResponseStatusException.class,
                () -> usuarioService.crear(new CrearUsuarioRequest("A".repeat(51), "a@rinconsur.cl", "Segura123!", 1L)));
        assertThrows(ResponseStatusException.class,
                () -> usuarioService.crear(new CrearUsuarioRequest("Pedro123", "a@rinconsur.cl", "Segura123!", 1L)));
    }

    @Test
    void debeValidarCorreoCorrectamente() {
        assertEquals("", usuarioService.normalizarCorreo(null));
        assertThrows(ResponseStatusException.class, () -> usuarioService.validarCorreo(""));
        assertThrows(ResponseStatusException.class, () -> usuarioService.validarCorreo("a".repeat(45) + "@rinconsur.cl"));
        assertThrows(ResponseStatusException.class, () -> usuarioService.validarCorreo("correosinarroba"));
        assertThrows(ResponseStatusException.class, () -> usuarioService.validarCorreo("juan@gmail.com"));
    }

    @Test
    void debeValidarClaveCorrectamente() {
        assertThrows(ResponseStatusException.class, () -> usuarioService.validarClave(null));
        assertThrows(ResponseStatusException.class, () -> usuarioService.validarClave("   "));
        assertThrows(ResponseStatusException.class, () -> usuarioService.validarClave("debil123"));
    }

    // ==========================================
    // LOGIN Y CONTRASEÑAS
    // ==========================================
    @Test
    void debeIniciarSesionExitosamente() {
        LoginRequest login = new LoginRequest("juan.perez@rinconsur.cl", "Segura123!");
        when(usuarioRepository.findByCorreo(anyString())).thenReturn(Optional.of(usuarioFalso));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        UsuarioResponse response = usuarioService.iniciarSesion(login);
        assertEquals("juan.perez@rinconsur.cl", response.correo());
    }

    @Test
    void debeFallarLoginSiCuentaEstaInactiva() {
        usuarioFalso.setActivo(false);
        LoginRequest login = new LoginRequest("juan.perez@rinconsur.cl", "Segura123!");
        when(usuarioRepository.findByCorreo(anyString())).thenReturn(Optional.of(usuarioFalso));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> usuarioService.iniciarSesion(login));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void debeFallarLoginSiClaveEsNulaOIncorrecta() {
        when(usuarioRepository.findByCorreo(anyString())).thenReturn(Optional.of(usuarioFalso));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> usuarioService.iniciarSesion(new LoginRequest("juan.perez@rinconsur.cl", null)));
        assertThrows(ResponseStatusException.class,
                () -> usuarioService.iniciarSesion(new LoginRequest("juan.perez@rinconsur.cl", "Mal123!")));
    }

    @Test
    void debeCambiarContrasenaYFallarSiActualEsIncorrecta() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioFalso));
        when(passwordEncoder.matches("vieja123", "hash123")).thenReturn(true);
        when(passwordEncoder.encode("Nueva123!")).thenReturn("newHash");

        usuarioService.cambiarContrasena(1L, new CambiarContrasenaRequest("vieja123", "Nueva123!"));
        verify(usuarioRepository, times(1)).save(usuarioFalso);

        assertThrows(ResponseStatusException.class,
                () -> usuarioService.cambiarContrasena(1L, new CambiarContrasenaRequest(null, "Nueva123!")));
        assertThrows(ResponseStatusException.class,
                () -> usuarioService.cambiarContrasena(1L, new CambiarContrasenaRequest("incorrecta", "Nueva123!")));
    }

    // ==========================================
    // MODIFICACIÓN DE USUARIO Y PERFIL
    // ==========================================
    @Test
    void debeCambiarRol() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioFalso));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolFalso));
        when(usuarioRepository.save(any())).thenReturn(usuarioFalso);

        assertNotNull(usuarioService.cambiarRol(1L, new CambiarRolRequest(1L)));
        assertThrows(ResponseStatusException.class, () -> usuarioService.cambiarRol(1L, new CambiarRolRequest(null)));
    }

    @Test
    void debeCambiarEstado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioFalso));
        when(usuarioRepository.save(any())).thenReturn(usuarioFalso);

        assertNotNull(usuarioService.cambiarEstado(1L, new CambiarEstadoRequest(false)));
        assertThrows(ResponseStatusException.class, () -> usuarioService.cambiarEstado(1L, new CambiarEstadoRequest(null)));
    }

    @Test
    void debeActualizarPerfilYValidarCorreoDuplicado() {
        ActualizarPerfilRequest request = new ActualizarPerfilRequest("Juan Modificado", "juan.modificado@rinconsur.cl");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioFalso));
        when(usuarioRepository.existsByCorreoAndIdNot(anyString(), eq(1L))).thenReturn(false);
        when(usuarioRepository.save(any())).thenReturn(usuarioFalso);

        assertNotNull(usuarioService.actualizarPerfil(1L, request));

        when(usuarioRepository.existsByCorreoAndIdNot(anyString(), eq(1L))).thenReturn(true);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> usuarioService.actualizarPerfil(1L, request));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    // ==========================================
    // FOTOS Y MÉTODOS AUXILIARES DE RECUPERACIÓN
    // ==========================================
    @Test
    void debeSubirFotoYConstruirUrlConFotoPerfil() {
        MultipartFile archivo = mock(MultipartFile.class);
        usuarioFalso.setFotoPerfil("vieja.jpg");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioFalso));
        when(almacenamientoService.guardar(archivo)).thenReturn("nueva.jpg");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponse response = usuarioService.subirFoto(1L, archivo);

        assertNotNull(response);
        verify(almacenamientoService, times(1)).eliminar("vieja.jpg");
    }

    @Test
    void debeObtenerFotoYFallarSiNoTiene() {
        usuarioFalso.setFotoPerfil("foto.jpg");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioFalso));
        when(almacenamientoService.leer("foto.jpg")).thenReturn(new byte[]{1, 2, 3});

        assertEquals(3, usuarioService.obtenerFoto(1L).length);

        usuarioFalso.setFotoPerfil(null);
        assertThrows(ResponseStatusException.class, () -> usuarioService.obtenerFoto(1L));
    }

    @Test
    void debeBuscarPorCorreoYGuardarContrasena() {
        when(usuarioRepository.findByCorreo("juan.perez@rinconsur.cl")).thenReturn(Optional.of(usuarioFalso));
        when(usuarioRepository.findByCorreo("noexiste@rinconsur.cl")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("ClaveNueva1!")).thenReturn("encodedNueva");

        assertEquals(usuarioFalso, usuarioService.buscarPorCorreo("juan.perez@rinconsur.cl"));
        assertThrows(ResponseStatusException.class, () -> usuarioService.buscarPorCorreo("noexiste@rinconsur.cl"));

        usuarioService.guardarContrasena(usuarioFalso, "ClaveNueva1!");
        assertEquals("encodedNueva", usuarioFalso.getContrasena());
        verify(usuarioRepository, times(1)).save(usuarioFalso);
    }
}