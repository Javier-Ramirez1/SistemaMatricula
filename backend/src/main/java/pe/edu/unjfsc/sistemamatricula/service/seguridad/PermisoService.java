package pe.edu.unjfsc.sistemamatricula.service.seguridad;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.dto.seguridad.PermisoResponse;
import pe.edu.unjfsc.sistemamatricula.entity.seguridad.Permiso;
import pe.edu.unjfsc.sistemamatricula.repository.PermisoRepository;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Catálogo de permisos atómicos (modulo/submodulo/accion) — alimenta
 * el árbol de checkboxes del panel "Asignar Permisos" en la pantalla
 * de Superusuario del boceto.
 */
@Service
@RequiredArgsConstructor
public class PermisoService {

    private final PermisoRepository permisoRepository;

    @Transactional(readOnly = true)
    public List<PermisoResponse> listarTodos() {
        return permisoRepository.findByEstado(Constantes.ACTIVO).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private PermisoResponse toResponse(Permiso p) {
        return new PermisoResponse(p.getId(), p.getModulo(), p.getSubmodulo(), p.getAccion(), p.getDescripcion());
    }
}