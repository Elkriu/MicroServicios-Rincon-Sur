package com.rinconsur.dashboard.client;

import com.rinconsur.dashboard.dto.RegistroDetalleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VisitanteClient {

    private final WebClient visitanteWebClient;

    public List<RegistroDetalleResponse> listarRegistros(String fecha, String acceso) {
        try {
            List<RegistroDetalleResponse> registros = visitanteWebClient.get()
                    .uri(uri -> uri.path("/api/registros")
                            .queryParamIfPresent("fecha", Optional.ofNullable(fecha))
                            .queryParamIfPresent("acceso", Optional.ofNullable(acceso))
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<RegistroDetalleResponse>>() { })
                    .block();
            return registros == null ? List.of() : registros;
        } catch (WebClientResponseException.ServiceUnavailable e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo contactar con el servicio de visitantes");
        } catch (WebClientRequestException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo contactar con el servicio de visitantes");
        } catch (WebClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "El servicio de visitantes respondió con error");
        }
    }

    public List<String> listarFechas() {
        try {
            List<String> fechas = visitanteWebClient.get()
                    .uri("/api/registros/fechas")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() { })
                    .block();
            return fechas == null ? List.of() : fechas;
        } catch (WebClientResponseException.ServiceUnavailable e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo contactar con el servicio de visitantes");
        } catch (WebClientRequestException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo contactar con el servicio de visitantes");
        } catch (WebClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "El servicio de visitantes respondió con error");
        }
    }
}