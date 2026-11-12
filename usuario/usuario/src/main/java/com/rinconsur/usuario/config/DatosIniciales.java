package com.rinconsur.usuario.config;

import com.rinconsur.usuario.model.Rol;
import com.rinconsur.usuario.model.Usuario;
import com.rinconsur.usuario.repository.RolRepository;
import com.rinconsur.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DatosIniciales implements CommandLineRunner {

    private static final String CLAVE_INICIAL = "Rincor.2026!";

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (rolRepository.count() == 0) {
            rolRepository.saveAll(List.of(
                    new Rol(Rol.ID_OBSERVADOR, "Observador de Pasillo"),
                    new Rol(Rol.ID_JEFE_TIENDA, "Jefe de Tienda"),
                    new Rol(Rol.ID_ADMINISTRADOR, "Administrador")
            ));
        }

        if (usuarioRepository.count() == 0) {
            String hash = passwordEncoder.encode(CLAVE_INICIAL);
            usuarioRepository.saveAll(List.of(
                    usuario("Tamara Ramírez", "observador@rinconsur.cl", hash, Rol.ID_OBSERVADOR),
                    usuario("Rodrigo Saavedra", "supervisor@rinconsur.cl", hash, Rol.ID_JEFE_TIENDA),
                    usuario("Administrador IT", "admin@rinconsur.cl", hash, Rol.ID_ADMINISTRADOR)
            ));
        }
    }

    private Usuario usuario(String nombre, String correo, String hash, Long rolId) {
        return Usuario.builder()
                .nombre(nombre)
                .correo(correo)
                .contrasena(hash)
                .rol(rolRepository.getReferenceById(rolId))
                .build();
    }
}