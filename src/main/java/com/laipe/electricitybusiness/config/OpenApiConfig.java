package com.laipe.electricitybusiness.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String cookieAuthName = "cookieAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Electricity Business API")
                        .description("API REST pour la gestion de bornes de recharge électrique. " +
                                "L'authentification se fait via un cookie httpOnly 'access_token' contenant le JWT. " +
                                "Connectez-vous via /api/auth/login pour obtenir le cookie automatiquement.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Laipe")
                                .email("contact@electricitybusiness.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(cookieAuthName))
                .components(new Components()
                        .addSecuritySchemes(cookieAuthName, new SecurityScheme()
                                .name("access_token")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .description("Cookie httpOnly 'access_token' contenant le JWT. " +
                                        "Le cookie est automatiquement envoyé par le navigateur après connexion via /api/auth/login.")));
    }
}

