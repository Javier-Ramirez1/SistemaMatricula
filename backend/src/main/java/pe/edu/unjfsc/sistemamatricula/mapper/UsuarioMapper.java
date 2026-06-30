package pe.edu.unjfsc.sistemamatricula.mapper;

import org.springframework.stereotype.Component;
import pe.edu.unjfsc.sistemamatricula.dto.seguridad.UsuarioResponse;
import pe.edu.unjfsc.sistemamatricula.entity.seguridad.Rol;
import pe.edu.unjfsc.sistemamatricula.entity.seguridad.Usuario;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public UsuarioResponse toResponse(Usuario usuario) {
        if (usuario == null) return null;
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                usuario.getEmail(),
                usuario.getTipoDocumento() != null ? usuario.getTipoDocumento().getCodigo() : null,
                usuario.getNumeroDocumento(),
                usuario.getEsSuperusuario() != null && usuario.getEsSuperusuario() == 1,
                usuario.getRoles().stream().map(Rol::getNombre).collect(Collectors.toList()),
                usuario.getEstado(),
                usuario.getVersion(),
                usuario.getFechaInsert()
        );
    }

    public boolean esActivo(Usuario usuario) {
        return usuario.getEstado() != null && usuario.getEstado() == Constantes.ACTIVO;
    }
}