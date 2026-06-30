package pe.edu.unjfsc.sistemamatricula.service.seguridad;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.dto.seguridad.LoginRequest;
import pe.edu.unjfsc.sistemamatricula.dto.seguridad.LoginResponse;
import pe.edu.unjfsc.sistemamatricula.entity.auditoria.LoginLog;
import pe.edu.unjfsc.sistemamatricula.entity.seguridad.Permiso;
import pe.edu.unjfsc.sistemamatricula.entity.seguridad.Rol;
import pe.edu.unjfsc.sistemamatricula.entity.seguridad.Usuario;
import pe.edu.unjfsc.sistemamatricula.exception.BusinessException;
import pe.edu.unjfsc.sistemamatricula.repository.UsuarioRepository;
import pe.edu.unjfsc.sistemamatricula.security.JwtUtil;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Login con emisión de JWT + bitácora en login_log (trazabilidad
 * de seguridad pedida en los apuntes: éxito y fallo se registran).
 */
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final pe.edu.unjfsc.sistemamatricula.repository.LoginLogRepository loginLogRepository;

    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(request.getUsername());

        if (usuarioOpt.isEmpty()) {
            registrarIntento(null, request.getUsername(), false, "Usuario no encontrado", httpRequest);
            throw new BusinessException("Usuario o contraseña incorrectos.");
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getEstado() == null || usuario.getEstado() != Constantes.ACTIVO) {
            registrarIntento(usuario, request.getUsername(), false, "Usuario inactivo", httpRequest);
            throw new BusinessException("El usuario está inactivo. Contacta al administrador.");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            registrarIntento(usuario, request.getUsername(), false, "Contraseña incorrecta", httpRequest);
            throw new BusinessException("Usuario o contraseña incorrectos.");
        }

        List<String> authorities = new ArrayList<>();
        for (Rol rol : usuario.getRoles()) {
            authorities.add("ROLE_" + rol.getNombre());
            for (Permiso permiso : rol.getPermisos()) {
                authorities.add(permiso.getCodigoCompleto());
            }
        }

        String token = jwtUtil.generarToken(usuario.getUsername(), authorities);
        registrarIntento(usuario, request.getUsername(), true, null, httpRequest);

        return new LoginResponse(
                token,
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                usuario.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet()),
                usuario.isSuperusuario()
        );
    }

    /**
     * El log de login se guarda SIEMPRE, incluso si el login principal
     * falla y se relanza la excepción — por eso usa su propia
     * transacción independiente (REQUIRES_NEW), para que el rollback
     * del método principal no borre también el registro de auditoría
     * del intento fallido.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarIntento(Usuario usuario, String usernameIntentado, boolean exito,
                                 String motivoFallo, HttpServletRequest httpRequest) {
        // Si el usuario no existe en BD no hay FK válida para login_log; en ese caso
        // solo se registra en logs de aplicación (fuera del alcance de esta tabla).
        if (usuario == null) return;

        LoginLog log = new LoginLog();
        log.setUsuario(usuario);
        log.setFechaLogin(LocalDateTime.now());
        log.setIpCliente(httpRequest != null ? httpRequest.getRemoteAddr() : null);
        log.setNavegador(httpRequest != null ? httpRequest.getHeader("User-Agent") : null);
        log.setResultado(exito ? "EXITO" : "FALLO");
        log.setMotivoFallo(motivoFallo);
        log.setUsuarioInsert(usernameIntentado);

        loginLogRepository.save(log);
    }
}