package com.rinconsur.visitante.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient usuarioWebClient(@Value("${app.ms-usuario.url}") String urlBase) {
        return WebClient.builder()
                .baseUrl(urlBase)
                .build();
    }
}