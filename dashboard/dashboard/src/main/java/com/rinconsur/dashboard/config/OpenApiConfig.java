package com.rinconsur.dashboard.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("API de Dashboard - Rincón Sur")
                .description("Microservicio de dashboard: resúmenes y distribuciones del conteo de visitantes. "
                        + "No guarda datos propios: consulta los registros al servicio de visitantes mediante WebClient "
                        + "y calcula los totales en cada petición.")
                .version("1.0.0"));
    }
}