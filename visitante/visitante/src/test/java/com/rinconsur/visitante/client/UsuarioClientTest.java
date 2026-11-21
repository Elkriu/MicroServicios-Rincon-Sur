package com.rinconsur.visitante.client;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.server.ResponseStatusException;

import com.rinconsur.visitante.dto.UsuarioResponse;

import reactor.core.publisher.Mono;

class UsuarioClientTest {

    private UsuarioClient crearClienteCon(ExchangeFunction exchangeFunction) {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8081")
                .exchangeFunction(exchangeFunction)
                .build();
        return new UsuarioClient(webClient);
    }

    @Test
    void debeObtenerUsuarioExitosamente() {
        String json = """
                {
                    "id": 10,
                    "nombre": "Carlos",
                    "correo": "carlos@rinconsur.cl",
                    "rolId": 1,
                    "activo": true
                }
                """;

        UsuarioClient client = crearClienteCon(request -> Mono.just(
                ClientResponse.create(HttpStatus.OK)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(json)
                        .build()
        ));

        UsuarioResponse resp = client.obtener(10L);

        assertNotNull(resp);
        assertEquals(1L, resp.rolId());
        assertTrue(resp.activo());
    }

    @Test
    void debeLanzarNotFoundCuandoUsuarioNoExiste() {
        UsuarioClient client = crearClienteCon(request -> Mono.just(
                ClientResponse.create(HttpStatus.NOT_FOUND)
                        .body("No encontrado")
                        .build()
        ));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> client.obtener(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void debeLanzarBadGatewayCuandoServicioUsuariosFalla() {
        UsuarioClient client = crearClienteCon(request -> Mono.just(
                ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error interno")
                        .build()
        ));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> client.obtener(10L));
        assertEquals(HttpStatus.BAD_GATEWAY, ex.getStatusCode());
    }

    @Test
    void debeLanzarServiceUnavailableCuandoNoHayConexion() {
        UsuarioClient client = crearClienteCon(request -> Mono.error(
                new WebClientRequestException(
                        new RuntimeException("Connection refused"),
                        HttpMethod.GET,
                        URI.create("http://localhost:8081/api/usuarios/10"),
                        new HttpHeaders()
                )
        ));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> client.obtener(10L));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
    }
}