package pe.edu.unjfsc.sistemamatricula.service.seguridad;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.dto.seguridad.*;
import pe.edu.unjfsc.sistemamatricula.entity.seguridad.Permiso;
import pe.edu.unjfsc.sistemamatricula.entity.seguridad.Rol;
import pe.edu.unjfsc.sistemamatricula.exception.BusinessException;
import pe.edu.unjfsc.sistemamatricula.exception.ResourceNotFoundException;
import pe.edu.unjfsc.sistemamatricula.repository.PermisoRepository;
import pe.edu.unjfsc.sistemamatricula.repository.RolRepository;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Gestión de roles y, sobre todo, del modal "Asignar Permisos" del
 * boceto: checkboxes por módulo + botón "Aplicar" que sobrescribe
 * de una sola vez el set completo de permisos del rol.
 */
@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;

    @Transactional(readOnly = true)
    public List<RolResponse> listarTodos() {
        return rolRepository.findByEstado(Constantes.ACTIVO).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RolResponse obtenerPorId(Long id) {
        return toResponse(buscarOLanzar(id));
    }

    @Transactional
    public RolResponse crear(RolRequest request) {
        if (rolRepository.findByNombre(request.getNombre()).isPresent()) {
            throw new BusinessException("Ya existe un rol con el nombre '" + request.getNombre() + "'.");
        }
        Rol rol = new Rol();
        rol.setNombre(request.getNombre());
        rol.setDescripcion(request.getDescripcion());
        rol.setEstado(Constantes.ACTIVO);
        return toResponse(rolRepository.save(rol));
    }

    /**
     * Aplica de golpe la selección de checkboxes del modal de permisos:
     * reemplaza COMPLETAMENTE el set de permisos del rol por el recibido.
     * Equivale al botón "Aplicar" del boceto.
     */
    @Transactional
    public RolResponse asignarPermisos(AsignarPermisosRequest request) {
        Rol rol = buscarOLanzar(request.getIdRol());

        if ("SUPERUSUARIO".equalsIgnoreCase(rol.getNombre())) {
            throw new BusinessException("El rol SUPERUSUARIO siempre tiene acceso total; no se le pueden restringir permisos.");
        }

        Set<Permiso> nuevosPermisos = new HashSet<>();
        for (Long idPermiso : request.getIdsPermisosSeleccionados()) {
            nuevosPermisos.add(permisoRepository.findById(idPermiso)
                    .orElseThrow(() -> new ResourceNotFoundException("Permiso", idPermiso)));
        }

        rol.setPermisos(nuevosPermisos);
        return toResponse(rolRepository.save(rol));
    }

    private RolResponse toResponse(Rol rol) {
        List<PermisoResponse> permisos = rol.getPermisos().stream()
                .map(p -> new PermisoResponse(p.getId(), p.getModulo(), p.getSubmodulo(), p.getAccion(), p.getDescripcion()))
                .collect(Collectors.toList());
        return new RolResponse(rol.getId(), rol.getNombre(), rol.getDescripcion(), rol.getEstado(), rol.getVersion(), permisos);
    }

    private Rol buscarOLanzar(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", id));
    }
}