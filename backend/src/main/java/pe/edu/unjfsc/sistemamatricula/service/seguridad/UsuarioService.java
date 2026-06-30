package pe.edu.unjfsc.sistemamatricula.service.seguridad;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.dto.seguridad.*;
import pe.edu.unjfsc.sistemamatricula.entity.academico.TipoDocumento;
import pe.edu.unjfsc.sistemamatricula.entity.seguridad.Rol;
import pe.edu.unjfsc.sistemamatricula.entity.seguridad.Usuario;
import pe.edu.unjfsc.sistemamatricula.exception.BusinessException;
import pe.edu.unjfsc.sistemamatricula.exception.ResourceNotFoundException;
import pe.edu.unjfsc.sistemamatricula.mapper.UsuarioMapper;
import pe.edu.unjfsc.sistemamatricula.repository.RolRepository;
import pe.edu.unjfsc.sistemamatricula.repository.TipoDocumentoRepository;
import pe.edu.unjfsc.sistemamatricula.repository.UsuarioRepository;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;
import pe.edu.unjfsc.sistemamatricula.validation.DocumentoValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Gestión de usuarios del sistema (panel "Gestión de Usuarios" del
 * boceto). Reglas clave:
 *  - El superusuario NUNCA se puede eliminar (ni lógicamente).
 *  - El borrado es siempre lógico (estado=0), nunca DELETE físico,
 *    para preservar la integridad referencial de auditoría/recibos
 *    firmados por ese usuario en años anteriores.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final DocumentoValidator documentoValidator;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        return usuarioMapper.toResponse(buscarOLanzar(id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("El username '" + request.getUsername() + "' ya está en uso.");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BusinessException("La contraseña es obligatoria al crear un usuario.");
        }

        TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(request.getIdTipoDocumento())
                .orElseThrow(() -> new ResourceNotFoundException("TipoDocumento", request.getIdTipoDocumento()));

        if (!documentoValidator.esValidoSegunTipo(tipoDocumento.getCodigo(), request.getNumeroDocumento())) {
            throw new BusinessException("El número de documento no es válido para el tipo " + tipoDocumento.getCodigo());
        }

        if (usuarioRepository.existsByTipoDocumentoIdAndNumeroDocumento(
                request.getIdTipoDocumento(), request.getNumeroDocumento())) {
            throw new BusinessException("Ya existe un usuario registrado con ese tipo y número de documento.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setEmail(request.getEmail());
        usuario.setTipoDocumento(tipoDocumento);
        usuario.setNumeroDocumento(request.getNumeroDocumento());
        usuario.setEsSuperusuario((byte) 0);
        usuario.setEstado(Constantes.ACTIVO);
        usuario.setRoles(resolverRoles(request.getIdsRoles()));

        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscarOLanzar(id);

        if (!usuario.getUsername().equals(request.getUsername())
                && usuarioRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("El username '" + request.getUsername() + "' ya está en uso.");
        }

        usuario.setUsername(request.getUsername());
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        // Un superusuario conserva siempre todos sus permisos: no se le quitan roles por esta vía.
        if (!usuario.isSuperusuario()) {
            usuario.setRoles(resolverRoles(request.getIdsRoles()));
        }

        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    /** Borrado lógico — JAMÁS DELETE físico (regla de auditoría e integridad referencial). */
    @Transactional
    public void eliminarLogico(Long id) {
        Usuario usuario = buscarOLanzar(id);

        if (usuario.isSuperusuario()) {
            throw new BusinessException("El superusuario no puede ser eliminado del sistema.");
        }

        usuario.setEstado(Constantes.INACTIVO);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void restaurar(Long id) {
        Usuario usuario = buscarOLanzar(id);
        usuario.setEstado(Constantes.ACTIVO);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarPassword(String username, CambiarPasswordRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", 0L));

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPasswordHash())) {
            throw new BusinessException("La contraseña actual es incorrecta.");
        }

        usuario.setPasswordHash(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);
    }

    private Set<Rol> resolverRoles(List<Long> idsRoles) {
        Set<Rol> roles = new HashSet<>();
        for (Long idRol : idsRoles) {
            roles.add(rolRepository.findById(idRol)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol", idRol)));
        }
        return roles;
    }

    private Usuario buscarOLanzar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }
}