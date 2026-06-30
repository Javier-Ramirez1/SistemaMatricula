package pe.edu.unjfsc.sistemamatricula.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Habilita la auditoría automática de JPA.
 *
 * Con @EnableJpaAuditing + AuditorAware, Spring rellena SOLO
 * automáticamente los campos marcados con @CreatedBy / @LastModifiedBy
 * en las entidades, tomando el username del usuario autenticado
 * desde el SecurityContext (el que dejó el JwtFilter).
 *
 * Esto evita tener que pasar manualmente "usuarioSesion" a cada
 * Service — Hibernate lo hace solo en el @PrePersist / @PreUpdate.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return Optional.of("SISTEMA");  // procesos batch/seed sin usuario logueado
            }
            return Optional.of(auth.getName());  // username del JWT
        };
    }
}