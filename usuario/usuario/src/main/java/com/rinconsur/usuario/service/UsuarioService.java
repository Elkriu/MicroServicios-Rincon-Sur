package com.rinconsur.usuario.service;

import com.rinconsur.usuario.dto.CambiarContrasenaRequest;
import com.rinconsur.usuario.dto.ActualizarPerfilRequest;
import org.springframework.web.multipart.MultipartFile;
import com.rinconsur.usuario.dto.LoginRequest;
import com.rinconsur.usuario.dto.CrearUsuarioRequest;
import com.rinconsur.usuario.dto.RolResponse;
import com.rinconsur.usuario.dto.UsuarioResponse;
import com.rinconsur.usuario.model.Rol;
import com.rinconsur.usuario.model.Usuario;
import com.rinconsur.usuario.repository.RolRepository;
import com.rinconsur.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final String DOMINIO = "@rinconsur.cl";
    private static final int CORREO_MAX = 50;
    private static final int NOMBRE_MAX = 50;

    private static final Pattern PATRON_NOMBRE = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");

    private static final Pattern PATRON_CLAVE =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!._\\-]).{8,}$");

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AlmacenamientoService almacenamientoService;

    public List<RolResponse> listarRoles() {
        return rolRepository.findAll().stream()
                .map(rol -> new RolResponse(rol.getId(), rol.getNombre()))
                .toList();
    }

    public List<UsuarioResponse> listar(String buscar, Long rolId, Boolean activo) {
        List<Usuario> usuarios = (buscar == null || buscar.isBlank())
                ? usuarioRepository.findAllByOrderByNombreAsc()
                : usuarioRepository.findByNombreContainingIgnoreCaseOrCorreoContainingIgnoreCaseOrderByNombreAsc(buscar, buscar);

        return usuarios.stream()
                .filter(u -> rolId == null || u.getRol().getId().equals(rolId))
                .filter(u -> activo == null || u.isActivo() == activo)
                .map(this::aRespuesta)
                .toList();
    }

    public UsuarioResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    public UsuarioResponse crear(CrearUsuarioRequest datos) {
        String correo = normalizarCorreo(datos.correo());
        validarNombre(datos.nombre());
        validarCorreo(correo);
        validarClave(datos.contrasena());
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya está registrado");
        }

        Long rolId = datos.rolId() != null ? datos.rolId() : Rol.ID_OBSERVADOR;
        Usuario nuevo = Usuario.builder()
                .nombre(datos.nombre().trim())
                .correo(correo)
                .contrasena(passwordEncoder.encode(datos.contrasena()))
                .rol(buscarRol(rolId))
                .build();
        return aRespuesta(usuarioRepository.save(nuevo));
    }

    
    public UsuarioResponse iniciarSesion(LoginRequest datos) {
        String correo = normalizarCorreo(datos.correo());
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .filter(u -> datos.contrasena() != null && passwordEncoder.matches(datos.contrasena(), u.getContrasena()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos"));

        if (!usuario.isActivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu cuenta ha sido suspendida. Contacta al Administrador.");
        }
        return aRespuesta(usuario);
    }

    public void cambiarContrasena(Long id, CambiarContrasenaRequest datos) {
        Usuario usuario = buscar(id);

        if (datos.actual() == null || !passwordEncoder.matches(datos.actual(), usuario.getContrasena())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La contraseña actual es incorrecta");
        }
        validarClave(datos.nueva());

        usuario.setContrasena(passwordEncoder.encode(datos.nueva()));
        usuarioRepository.save(usuario);
    }


    public UsuarioResponse actualizarPerfil(Long id, ActualizarPerfilRequest datos) {
        Usuario usuario = buscar(id);
        String correo = normalizarCorreo(datos.correo());

        validarNombre(datos.nombre());
        validarCorreo(correo);
        if (usuarioRepository.existsByCorreoAndIdNot(correo, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya está registrado");
        }

        usuario.setNombre(datos.nombre().trim());
        usuario.setCorreo(correo);
        return aRespuesta(usuarioRepository.save(usuario));
    }

    public UsuarioResponse subirFoto(Long id, MultipartFile archivo) {
        Usuario usuario = buscar(id);
        String anterior = usuario.getFotoPerfil();

        usuario.setFotoPerfil(almacenamientoService.guardar(archivo));
        Usuario guardado = usuarioRepository.save(usuario);

        almacenamientoService.eliminar(anterior); // borra la foto vieja para no acumular archivos
        return aRespuesta(guardado);
    }

    public byte[] obtenerFoto(Long id) {
        Usuario usuario = buscar(id);
        if (usuario.getFotoPerfil() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no tiene foto");
        }
        return almacenamientoService.leer(usuario.getFotoPerfil());
    }


    public Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    public String normalizarCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase();
    }

    public void validarCorreo(String correo) {
        if (correo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo es obligatorio");
        }
        if (correo.length() > CORREO_MAX) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no puede superar los " + CORREO_MAX + " caracteres");
        }
        if (!correo.contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingresa un correo válido");
        }
        if (!correo.endsWith(DOMINIO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes usar tu correo institucional " + DOMINIO);
        }
    }

    public void validarClave(String clave) {
        if (clave == null || clave.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
        }
        if (!PATRON_CLAVE.matcher(clave).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener mínimo 8 caracteres, con mayúscula, minúscula y un símbolo (@#$%^&+=!._-)");
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre completo es obligatorio");
        }
        if (nombre.length() > NOMBRE_MAX) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre no puede superar los " + NOMBRE_MAX + " caracteres");
        }
        if (!PATRON_NOMBRE.matcher(nombre).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre solo puede contener letras y espacios");
        }
    }

    private Rol buscarRol(Long rolId) {
        return rolRepository.findById(rolId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol indicado no existe"));
    }

    private UsuarioResponse aRespuesta(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().getId(),
                usuario.getRol().getNombre(),
                urlFoto(usuario),
                usuario.isActivo()
        );
    }

    private String urlFoto(Usuario usuario) {
        if (usuario.getFotoPerfil() == null) {
            return null;
        }
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/usuarios/" + usuario.getId() + "/foto")
                .queryParam("v", usuario.getFotoPerfil())
                .toUriString();
    }

    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La cuenta no está registrada"));
    }

    public void guardarContrasena(Usuario usuario, String nueva) {
        usuario.setContrasena(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);
    }
}