package com.donation.donation_app.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private final Environment environment;

    public SwaggerConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "BearerAuth";

        if (isProdProfileActive()) {
            return createProdOpenAPI(securitySchemeName);
        } else {
            return createDevOpenAPI(securitySchemeName);
        }
    }

    private OpenAPI createProdOpenAPI(String securitySchemeName) {
        return new OpenAPI()
                .info(new Info()
                        .title("Appopay AML API")
                        .version("1.0")
                        .description("API documentation with JWT authentication"))
                .servers(List.of(
                        new Server().url("https://aml-backend.chenchenapp.com").description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    private OpenAPI createDevOpenAPI(String securitySchemeName) {
        return new OpenAPI()
                .info(new Info()
                        .title("Appopay AML API")
                        .version("1.0")
                        .description("API documentation with JWT authentication"))
                .servers(List.of(
                        new Server().url("https://Uatapi.ozerdal.org/").description("dev Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    private boolean isProdProfileActive() {
        return List.of(environment.getActiveProfiles()).contains("prod");
    }
}
