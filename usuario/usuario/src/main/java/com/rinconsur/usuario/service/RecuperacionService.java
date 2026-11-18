package com.rinconsur.usuario.service;

import com.rinconsur.usuario.dto.CodigoResponse;
import com.rinconsur.usuario.dto.RestablecerContrasenaRequest;
import com.rinconsur.usuario.dto.SolicitarCodigoRequest;
import com.rinconsur.usuario.dto.VerificarCodigoRequest;
import com.rinconsur.usuario.model.TokenRecuperacion;
import com.rinconsur.usuario.model.Usuario;
import com.rinconsur.usuario.repository.TokenRecuperacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class RecuperacionService {

    private final UsuarioService usuarioService;
    private final TokenRecuperacionRepository tokenRepository;

    @Value("${app.recuperacion.minutos}")
    private long minutosVigencia;

    @Value("${app.recuperacion.devolver-codigo}")
    private boolean devolverCodigo;

    private final SecureRandom azar = new SecureRandom();

    public CodigoResponse solicitar(SolicitarCodigoRequest datos) {
        String correo = usuarioService.normalizarCorreo(datos.correo());
        usuarioService.validarCorreo(correo);

        Usuario usuario = usuarioService.buscarPorCorreo(correo);
        if (!usuario.isActivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu cuenta ha sido suspendida. Contacta al Administrador.");
        }

        String codigo = String.valueOf(100000 + azar.nextInt(900000));
        tokenRepository.save(TokenRecuperacion.builder()
                .correo(correo)
                .codigo(codigo)
                .expira(LocalDateTime.now().plusMinutes(minutosVigencia))
                .build());

        // Todavía no se envía por correo: el código queda en la consola del servicio.
        log.info("Código de recuperación para {}: {}", correo, codigo);

        return new CodigoResponse("Código generado. Vence en " + minutosVigencia + " minutos.",
                devolverCodigo ? codigo : null);
    }

    /** Solo comprueba el código (no lo consume), para que la app pase a la pantalla de nueva clave. */
    public void verificar(VerificarCodigoRequest datos) {
        validarCodigo(usuarioService.normalizarCorreo(datos.correo()), datos.codigo());
    }

    public void restablecer(RestablecerContrasenaRequest datos) {
        String correo = usuarioService.normalizarCorreo(datos.correo());
        validarCodigo(correo, datos.codigo());
        usuarioService.validarClave(datos.nueva());

        Usuario usuario = usuarioService.buscarPorCorreo(correo);
        if (!usuario.isActivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu cuenta ha sido suspendida. Contacta al Administrador.");
        }
        usuarioService.guardarContrasena(usuario, datos.nueva());
        tokenRepository.deleteById(correo); // el código sirve una sola vez
    }

    private void validarCodigo(String correo, String codigo) {
        if (codigo == null || codigo.length() != 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código debe tener 6 dígitos");
        }
        TokenRecuperacion token = tokenRepository.findById(correo)
                .filter(t -> t.getCodigo().equals(codigo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código es incorrecto"));

        if (token.getExpira().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código venció, solicita uno nuevo");
        }
    }
}