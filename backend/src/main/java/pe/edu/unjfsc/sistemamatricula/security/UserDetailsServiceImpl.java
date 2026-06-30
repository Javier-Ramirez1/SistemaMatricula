package pe.edu.unjfsc.sistemamatricula.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.edu.unjfsc.sistemamatricula.entity.seguridad.Permiso;
import pe.edu.unjfsc.sistemamatricula.entity.seguridad.Rol;
import pe.edu.unjfsc.sistemamatricula.entity.seguridad.Usuario;
import pe.edu.unjfsc.sistemamatricula.repository.UsuarioRepository;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

import java.util.HashSet;
import java.util.Set;

/**
 * Carga el Usuario desde BD y construye sus authorities:
 *  - ROLE_<nombreRol> por cada rol asignado (ej. ROLE_SECRETARIA)
 *  - <modulo>_<submodulo>_<accion> por cada permiso fino dentro de
 *    esos roles (ej. FINANZAS_PAGO_PROCESAR), usado en @PreAuthorize
 *    de los métodos críticos.
 *
 * Solo se permite login si el usuario está activo (borrado lógico).
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsernameAndEstado(username, Constantes.ACTIVO)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o inactivo: " + username));

        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Rol rol : usuario.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
            for (Permiso permiso : rol.getPermisos()) {
                authorities.add(new SimpleGrantedAuthority(permiso.getCodigoCompleto()));
            }
        }

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .authorities(authorities)
                .disabled(usuario.getEstado() == null || usuario.getEstado() != Constantes.ACTIVO)
                .build();
    }
}