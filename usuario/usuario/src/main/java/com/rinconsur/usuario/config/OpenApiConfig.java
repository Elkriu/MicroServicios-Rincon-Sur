package com.rinconsur.usuario.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("API de Usuarios - Rincón Sur")
                .description("Microservicio de usuarios: gestión, login, perfil con foto, cambio de clave y "
                        + "recuperación de clave por código. Los roles viven dentro de este mismo servicio.")
                .version("1.0.0"));
    }
}