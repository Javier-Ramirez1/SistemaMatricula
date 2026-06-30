package pe.edu.unjfsc.sistemamatricula.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro que se ejecuta una vez por request (antes de
 * UsernamePasswordAuthenticationFilter, ver SecurityConfig).
 *
 * Lee el header "Authorization: Bearer <token>", lo valida con
 * JwtUtil y, si es correcto, registra al usuario en el
 * SecurityContext para que @PreAuthorize y los filtros de
 * SecurityConfig puedan evaluar sus roles/permisos.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(Constantes.HEADER_AUTORIZACION);

        if (header != null && header.startsWith(Constantes.PREFIJO_BEARER)) {
            String token = header.substring(Constantes.PREFIJO_BEARER.length());

            if (jwtUtil.esTokenValido(token)) {
                String username = jwtUtil.extraerUsername(token);
                List<String> authoritiesRaw = jwtUtil.extraerAuthorities(token);

                List<GrantedAuthority> authorities = authoritiesRaw == null
                        ? List.of()
                        : authoritiesRaw.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

                var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}