package com.rinconsur.dashboard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Builder que traduce los nombres de servicio (http://visitante) a una dirección real consultando a Eureka
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient visitanteWebClient(@LoadBalanced WebClient.Builder builder,
                                        @Value("${app.ms-visitante.url}") String urlBase) {
        return builder.clone().baseUrl(urlBase).build();
    }
}