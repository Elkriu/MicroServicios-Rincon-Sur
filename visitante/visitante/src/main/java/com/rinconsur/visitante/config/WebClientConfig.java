package com.rinconsur.visitante.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Builder que traduce los nombres de servicio (http://usuario) a una dirección real consultando a Eureka
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient usuarioWebClient(@LoadBalanced WebClient.Builder builder,
                                      @Value("${app.ms-usuario.url}") String urlBase) {
        return builder.clone().baseUrl(urlBase).build();
    }

    @Bean
    public WebClient administracionWebClient(@LoadBalanced WebClient.Builder builder,
                                             @Value("${app.ms-administracion.url}") String urlBase) {
        return builder.clone().baseUrl(urlBase).build();
    }
}