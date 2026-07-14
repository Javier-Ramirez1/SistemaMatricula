package com.proyecto.matricula.config.audit;

import com.proyecto.matricula.config.security.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of(1); // SUPERUSUARIO por defecto para el sistema
        }

        try {
            if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
                return Optional.of(userDetails.getIdUsuario());
            }
            return Optional.of(1);
        } catch (Exception e) {
            return Optional.of(1);
        }
    }
}
