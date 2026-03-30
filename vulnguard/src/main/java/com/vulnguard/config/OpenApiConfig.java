package com.vulnguard.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "VulnGuard API",
        version = "1.0",
        description = "Система управления уязвимостями (Vulnerability Management System). Позволяет отслеживать серверы, сканировать их на угрозы и управлять отчетами.",
        contact = @Contact(
            name = "Rodion",
            email = "support@vulnguard.com"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "Local Development Server"
        )
    }
)
public class OpenApiConfig {

}