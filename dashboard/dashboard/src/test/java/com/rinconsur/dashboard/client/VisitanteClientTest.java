package com.rinconsur.dashboard.client;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.List;

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

import com.rinconsur.dashboard.dto.RegistroDetalleResponse;

import reactor.core.publisher.Mono;

class VisitanteClientTest {

    private VisitanteClient crearClienteCon(ExchangeFunction exchangeFunction) {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8081")
                .exchangeFunction(exchangeFunction)
                .build();
        return new VisitanteClient(webClient);
    }

    @Test
    void debeListarRegistrosExitosamenteYConRespuestaVacia() {
        String json = """
                [
                    {
                        "id": 1,
                        "sesionId": 10,
                        "acceso": "Puerta Norte",
                        "fecha": "21-11-2026",
                        "hora": "10:00",
                        "clasificacion": "Adulto",
                        "rangoEtario": "30-45",
                        "compania": "Solo",
                        "bolsas": true,
                        "destino": "Supermercado"
                    }
                ]
                """;

        VisitanteClient client = crearClienteCon(request -> Mono.just(
                ClientResponse.create(HttpStatus.OK)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(json)
                        .build()
        ));

        List<RegistroDetalleResponse> registros = client.listarRegistros("21-11-2026", "Puerta Norte");
        assertEquals(1, registros.size());
        assertEquals("Puerta Norte", registros.get(0).acceso());

        VisitanteClient clientNulo = crearClienteCon(request -> Mono.just(
                ClientResponse.create(HttpStatus.OK).build()
        ));
        assertTrue(clientNulo.listarRegistros(null, null).isEmpty());
    }

    @Test
    void debeLanzarExcepcionesEnListarRegistros() {
        VisitanteClient clientError = crearClienteCon(request -> Mono.just(
                ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error interno")
                        .build()
        ));

        ResponseStatusException ex502 = assertThrows(ResponseStatusException.class,
                () -> clientError.listarRegistros("21-11-2026", null));
        assertEquals(HttpStatus.BAD_GATEWAY, ex502.getStatusCode());

        VisitanteClient clientSinConexion = crearClienteCon(request -> Mono.error(
                new WebClientRequestException(
                        new RuntimeException("Connection refused"),
                        HttpMethod.GET,
                        URI.create("http://localhost:8081/api/registros"),
                        new HttpHeaders()
                )
        ));

        ResponseStatusException ex503 = assertThrows(ResponseStatusException.class,
                () -> clientSinConexion.listarRegistros(null, null));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex503.getStatusCode());
    }

    @Test
    void debeListarFechasExitosamenteYConRespuestaVacia() {
        String json = """
                ["21-11-2026", "20-11-2026"]
                """;

        VisitanteClient client = crearClienteCon(request -> Mono.just(
                ClientResponse.create(HttpStatus.OK)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(json)
                        .build()
        ));

        List<String> fechas = client.listarFechas();
        assertEquals(2, fechas.size());
        assertEquals("21-11-2026", fechas.get(0));

        VisitanteClient clientNulo = crearClienteCon(request -> Mono.just(
                ClientResponse.create(HttpStatus.OK).build()
        ));
        assertTrue(clientNulo.listarFechas().isEmpty());
    }

    @Test
    void debeLanzarExcepcionesEnListarFechas() {
        VisitanteClient clientError = crearClienteCon(request -> Mono.just(
                ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error interno")
                        .build()
        ));

        ResponseStatusException ex502 = assertThrows(ResponseStatusException.class, clientError::listarFechas);
        assertEquals(HttpStatus.BAD_GATEWAY, ex502.getStatusCode());

        VisitanteClient clientSinConexion = crearClienteCon(request -> Mono.error(
                new WebClientRequestException(
                        new RuntimeException("Connection refused"),
                        HttpMethod.GET,
                        URI.create("http://localhost:8081/api/registros/fechas"),
                        new HttpHeaders()
                )
        ));

        ResponseStatusException ex503 = assertThrows(ResponseStatusException.class, clientSinConexion::listarFechas);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex503.getStatusCode());
    }
}