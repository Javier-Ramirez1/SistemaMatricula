package pe.edu.unjfsc.sistemamatricula.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * Generación y validación de JWT (firma HMAC-SHA).
 * El token lleva el username como subject y los roles como claim,
 * para que JwtFilter pueda reconstruir el contexto de seguridad
 * sin volver a consultar la BD en cada request (stateless real).
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expiracionMs;

    public JwtUtil(@Value("${app.jwt.secret}") String secret,
                   @Value("${app.jwt.expiration-ms}") long expiracionMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiracionMs = expiracionMs;
    }

    public String generarToken(String username, List<String> authorities) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiracionMs);

        return Jwts.builder()
                .subject(username)
                .claim("authorities", authorities)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(secretKey)
                .compact();
    }

    public Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extraerAuthorities(String token) {
        return (List<String>) extraerClaims(token).get("authorities");
    }

    public boolean esTokenValido(String token) {
        try {
            Claims claims = extraerClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}