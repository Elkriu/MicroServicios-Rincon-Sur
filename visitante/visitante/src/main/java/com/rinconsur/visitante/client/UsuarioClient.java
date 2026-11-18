package com.rinconsur.visitante.client;

import com.rinconsur.visitante.dto.UsuarioResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class UsuarioClient {

    private final WebClient usuarioWebClient;

    public UsuarioResponse obtener(Long id) {
        try {
            return usuarioWebClient.get()
                    .uri("/api/usuarios/{id}", id)
                    .retrieve()
                    .bodyToMono(UsuarioResponse.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El observador no existe");
        } catch (WebClientRequestException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo contactar con el servicio de usuarios");
        } catch (WebClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "El servicio de usuarios respondió con error");
        }
    }
}