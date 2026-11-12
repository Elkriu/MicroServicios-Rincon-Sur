package com.rinconsur.usuario.service;

import com.rinconsur.usuario.dto.RolResponse;
import com.rinconsur.usuario.dto.UsuarioResponse;
import com.rinconsur.usuario.model.Usuario;
import com.rinconsur.usuario.repository.RolRepository;
import com.rinconsur.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public List<RolResponse> listarRoles() {
        return rolRepository.findAll().stream()
                .map(rol -> new RolResponse(rol.getId(), rol.getNombre()))
                .toList();
    }

    /** Filtros opcionales: texto (nombre o correo), rol y estado. Ej.: observadores activos = ?rolId=1&activo=true */
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

    public Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
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

    // Por ahora siempre es null; cuando se agregue la foto (commit de perfil) ya queda funcionando
    private String urlFoto(Usuario usuario) {
        if (usuario.getFotoPerfil() == null) {
            return null;
        }
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/usuarios/" + usuario.getId() + "/foto")
                .queryParam("v", usuario.getFotoPerfil())
                .toUriString();
    }
}