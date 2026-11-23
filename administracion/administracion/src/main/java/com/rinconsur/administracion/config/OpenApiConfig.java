package com.rinconsur.administracion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("API de Administración - Rincón Sur")
                .description("Microservicio de administración: gestión de los accesos (entradas) de la tienda. "
                        + "Los demás servicios consultan aquí qué accesos existen y si están disponibles.")
                .version("1.0.0"));
    }
}