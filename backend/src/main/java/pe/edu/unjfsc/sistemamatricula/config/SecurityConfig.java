package pe.edu.unjfsc.sistemamatricula.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.edu.unjfsc.sistemamatricula.security.JwtFilter;

/**
 * Configuración central de Spring Security.
 *
 * STATELESS: no hay sesión HTTP — el estado vive en el JWT.
 * @EnableMethodSecurity habilita @PreAuthorize en los controllers
 * de cada submódulo (seguridad, academico, financiero, reportes).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login", "/api/auth/login",
                                "/css/**", "/js/**", "/img/**", "/icons/**",
                                "/plugins/**", "/vendor/**", "/error",
                                // Frontend estático: la seguridad real vive en la API (JWT),
                                // no en el acceso a estos archivos HTML/JS — el guard.js de
                                // cada dashboard valida sesión+rol en el cliente antes de
                                // pedir datos, y la API rechaza igual sin un JWT válido.
                                "/", "/index.html", "/login.html", "/favicon.ico",
                                "/superusuario/**", "/director/**", "/secretaria/**"
                        ).permitAll()

                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Módulo seguridad: solo SUPERUSUARIO administra usuarios/roles/permisos
                        .requestMatchers("/api/usuarios/**", "/api/roles/**", "/api/permisos/**")
                        .hasRole("SUPERUSUARIO")

                        // Módulo reportes: SUPERUSUARIO y DIRECTOR pueden consultar
                        .requestMatchers("/api/reportes/**")
                        .hasAnyRole("SUPERUSUARIO", "DIRECTOR")

                        // Procesos críticos (matrícula, pagos): solo quien tenga el permiso fino
                        // (validado además con @PreAuthorize en el controller/process)
                        .requestMatchers("/api/matricula/**", "/api/pagos/**")
                        .hasAnyRole("SUPERUSUARIO", "SECRETARIA")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}