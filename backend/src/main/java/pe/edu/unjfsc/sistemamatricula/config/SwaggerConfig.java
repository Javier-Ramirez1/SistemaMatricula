package pe.edu.unjfsc.sistemamatricula.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Documentación interactiva de la API en /swagger-ui.html
 *
 * Útil para probar los endpoints (login, matrícula, pagos) sin
 * tener que construir el frontend primero — y para que el profesor
 * vea la API documentada en la sustentación.
 *
 * Se agrega el esquema de seguridad "bearerAuth" para poder probar
 * endpoints protegidos directamente desde Swagger pegando el JWT.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Matrícula y Control de Cuentas — API")
                        .version("1.0")
                        .description("UNJFSC — Spring Boot + JWT + MySQL. " +
                                "Módulos: Seguridad, Académico, Finanzas, Reportes.")
                        .contact(new Contact().name("Frede").email("frede@unjfsc.edu.pe")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}