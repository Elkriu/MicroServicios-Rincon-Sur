package com.rinconsur.visitante.client;

import com.rinconsur.visitante.dto.AccesoResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AccesoClient {

    private final WebClient webClient;

    public AccesoClient(@Qualifier("administracionWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public AccesoResponse obtener(String nombre) {
        try {
            return webClient.get()
                    .uri("/api/accesos/{nombre}", nombre)
                    .retrieve()
                    .bodyToMono(AccesoResponse.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El acceso no existe");
        } catch (WebClientResponseException.ServiceUnavailable e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo contactar con el servicio de administración");
        } catch (WebClientRequestException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo contactar con el servicio de administración");
        } catch (WebClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "El servicio de administración respondió con error");
        }
    }
}