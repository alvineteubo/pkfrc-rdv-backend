package com.pkrfc.rdv_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final AppProperties appProperties;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addServersItem(
                        new Server()
                                .url(appProperties.address() + ":" + appProperties.port())
                                .description("Serveur local")
                )
                .info(new Info()
                        .title("Rendez-vous Backend API")
                        .version("1.0.0")
                        .description("API de gestion des rendez-vous")
                );
    }
}