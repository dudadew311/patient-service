package com.clinic.patient_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Patient Management Microservice API")
                        .version("1.0.0")
                        .description("REST API handling patient registrations, clinical lifecycle tracking, auditing, and soft-delete capabilities.")
                        .contact(new Contact()
                                .name("Clinical Engineering Support")));
    }
}