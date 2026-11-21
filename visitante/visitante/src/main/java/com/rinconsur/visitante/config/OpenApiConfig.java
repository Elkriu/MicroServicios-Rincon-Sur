package com.rinconsur.visitante.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("API de Visitantes - Rincón Sur")
                .description("Microservicio de visitantes: sesiones de conteo de un observador en un acceso de la tienda "
                        + "y los registros de cada visitante. Consulta al servicio de usuarios (observador) y al de "
                        + "administración (accesos) mediante WebClient.")
                .version("1.0.0"));
    }
}