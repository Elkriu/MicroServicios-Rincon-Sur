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

import com.rinconsur.visitante.dto.AccesoResponse;

import reactor.core.publisher.Mono;

class AccesoClientTest {

    private AccesoClient crearClienteCon(ExchangeFunction exchangeFunction) {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8082")
                .exchangeFunction(exchangeFunction)
                .build();
        return new AccesoClient(webClient);
    }

    @Test
    void debeObtenerAccesoExitosamente() {
        String json = """
                {
                    "id": 1,
                    "nombre": "Puerta Norte",
                    "disponible": true
                }
                """;

        AccesoClient client = crearClienteCon(request -> Mono.just(
                ClientResponse.create(HttpStatus.OK)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(json)
                        .build()
        ));

        AccesoResponse resp = client.obtener("Puerta Norte");

        assertNotNull(resp);
        assertTrue(resp.disponible());
    }

    @Test
    void debeLanzarNotFoundCuandoAccesoNoExiste() {
        AccesoClient client = crearClienteCon(request -> Mono.just(
                ClientResponse.create(HttpStatus.NOT_FOUND)
                        .body("No encontrado")
                        .build()
        ));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> client.obtener("Inexistente"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void debeLanzarBadGatewayCuandoServicioAdministracionFalla() {
        AccesoClient client = crearClienteCon(request -> Mono.just(
                ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error interno")
                        .build()
        ));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> client.obtener("Puerta Norte"));
        assertEquals(HttpStatus.BAD_GATEWAY, ex.getStatusCode());
    }

    @Test
    void debeLanzarServiceUnavailableCuandoNoHayConexion() {
        AccesoClient client = crearClienteCon(request -> Mono.error(
                new WebClientRequestException(
                        new RuntimeException("Connection refused"),
                        HttpMethod.GET,
                        URI.create("http://localhost:8082/api/accesos/Puerta%20Norte"),
                        new HttpHeaders()
                )
        ));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> client.obtener("Puerta Norte"));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
    }
}