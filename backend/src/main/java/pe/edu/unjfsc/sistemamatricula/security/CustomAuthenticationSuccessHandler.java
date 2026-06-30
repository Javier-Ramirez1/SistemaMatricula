package pe.edu.unjfsc.sistemamatricula.security;

/**
 * NOTA DE ARQUITECTURA:
 * El sistema usa JWT puro vía /api/auth/login (ver LoginController +
 * LoginService), NO el flujo de formLogin() de Spring Security.
 * Por eso este handler no se registra en SecurityConfig.
 *
 * Se conserva la clase para no romper el paquete ya creado y por si
 * en el futuro se agrega un login basado en sesión (ej. para el
 * panel Thymeleaf de Swagger/administración interna).
 */
public class CustomAuthenticationSuccessHandler {
}
