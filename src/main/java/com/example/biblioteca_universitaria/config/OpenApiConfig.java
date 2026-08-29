package com.example.biblioteca_universitaria.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bibliotecaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Biblioteca Universitária")
                        .description("CRUD de livros, empréstimos e reservas com autenticação JWT")
                        .version("1.0.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentação adicional")
                        .url("https://exemplo.com"));
    }
}
