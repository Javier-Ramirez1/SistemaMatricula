package com.proyecto.matricula.service;

import com.proyecto.matricula.entity.Auditoria;
import com.proyecto.matricula.entity.Usuario;
import com.proyecto.matricula.repository.AuditoriaRepository;
import com.proyecto.matricula.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository, UsuarioRepository usuarioRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void registrar(String modulo, String tabla, String operacion, Integer registroId, String valorAnterior, String valorNuevo) {
        Auditoria aud = new Auditoria();
        aud.setModulo(modulo);
        aud.setTablaAfectada(tabla);
        aud.setOperacion(operacion);
        aud.setCodigoRegistro(registroId);
        aud.setValorAnterior(valorAnterior);
        aud.setValorNuevo(valorNuevo);

        // Obtener IP y navegador de la petición HTTP
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            aud.setIpOrigen(request.getRemoteAddr());
            aud.setNavegador(request.getHeader("User-Agent"));
            aud.setEquipo(request.getRemoteHost());
        } else {
            aud.setIpOrigen("127.0.0.1");
            aud.setNavegador("Servidor (Sistema)");
            aud.setEquipo("localhost");
        }

        // Obtener usuario autenticado
        String currentUsername = SecurityContextHolder.getContext().getAuthentication() != null ?
                SecurityContextHolder.getContext().getAuthentication().getName() : "admin";
        
        Optional<Usuario> userOpt = usuarioRepository.findByUsernameAndEstadoTrue(currentUsername);
        userOpt.ifPresent(aud::setUsuario);

        auditoriaRepository.save(aud);
    }
}
