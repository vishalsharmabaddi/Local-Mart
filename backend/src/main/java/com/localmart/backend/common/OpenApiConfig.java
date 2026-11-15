package com.localmart.backend.common;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

// Swagger UI: http://localhost:8081/swagger-ui.html  (dev only; disabled in prod)
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "LocalMart API", version = "v1",
                description = "Town shops → village homes delivery. Login at /auth/login, then click Authorize."),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
