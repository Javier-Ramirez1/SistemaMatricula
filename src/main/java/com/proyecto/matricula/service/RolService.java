package com.proyecto.matricula.service;

import com.proyecto.matricula.entity.Funcionalidad;
import com.proyecto.matricula.entity.Rol;
import com.proyecto.matricula.entity.RolFuncionalidad;
import com.proyecto.matricula.repository.FuncionalidadRepository;
import com.proyecto.matricula.repository.RolFuncionalidadRepository;
import com.proyecto.matricula.repository.RolRepository;
import com.proyecto.matricula.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    private final RolRepository rolRepository;
    private final FuncionalidadRepository funcionalidadRepository;
    private final RolFuncionalidadRepository rolFuncionalidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    public RolService(RolRepository rolRepository,
                      FuncionalidadRepository funcionalidadRepository,
                      RolFuncionalidadRepository rolFuncionalidadRepository,
                      UsuarioRepository usuarioRepository,
                      AuditoriaService auditoriaService) {
        this.rolRepository = rolRepository;
        this.funcionalidadRepository = funcionalidadRepository;
        this.rolFuncionalidadRepository = rolFuncionalidadRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<Rol> listarActivos() {
        return rolRepository.findByEstadoTrue();
    }

    public Optional<Rol> obtenerPorId(Integer id) {
        return rolRepository.findByIdRolAndEstadoTrue(id);
    }

    @Transactional
    public Rol crearRol(String nombreRol, String descripcion) {
        // Validar si ya existe
        Optional<Rol> existente = rolRepository.findByNombreRolAndEstadoTrue(nombreRol.toUpperCase());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un rol con ese nombre.");
        }

        Rol nuevoRol = new Rol();
        nuevoRol.setNombreRol(nombreRol.toUpperCase().trim());
        nuevoRol.setDescripcion(descripcion);
        nuevoRol.setEstado(true);
        Rol guardado = rolRepository.save(nuevoRol);

        // Crear la matriz de permisos inicializada en false para todas las funcionalidades
        List<Funcionalidad> funcionalidades = funcionalidadRepository.findAllByEstadoTrue();
        for (Funcionalidad f : funcionalidades) {
            RolFuncionalidad rf = new RolFuncionalidad();
            rf.setRol(guardado);
            rf.setFuncionalidad(f);
            rf.setVer(false);
            rf.setCrear(false);
            rf.setEditar(false);
            rf.setEliminar(false);
            rf.setImprimir(false);
            rolFuncionalidadRepository.save(rf);
        }

        auditoriaService.registrar("Seguridad", "rol", "CREAR", guardado.getIdRol(), null, "Nombre: " + guardado.getNombreRol());
        return guardado;
    }

    @Transactional
    public Rol actualizarRol(Integer id, String nombreRol, String descripcion, List<RolFuncionalidad> permisosModificados) {
        Rol rol = rolRepository.findByIdRolAndEstadoTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("El rol no existe."));

        String valorAnterior = "Nombre: " + rol.getNombreRol() + ", Desc: " + rol.getDescripcion();
        rol.setNombreRol(nombreRol.toUpperCase().trim());
        rol.setDescripcion(descripcion);
        Rol guardado = rolRepository.save(rol);

        // Actualizar permisos
        if (permisosModificados != null) {
            for (RolFuncionalidad rfEditado : permisosModificados) {
                Optional<RolFuncionalidad> rfOpt = rolFuncionalidadRepository.findById(rfEditado.getIdRolFuncionalidad());
                if (rfOpt.isPresent()) {
                    RolFuncionalidad rf = rfOpt.get();
                    rf.setVer(rfEditado.getVer());
                    rf.setCrear(rfEditado.getCrear());
                    rf.setEditar(rfEditado.getEditar());
                    rf.setEliminar(rfEditado.getEliminar());
                    rf.setImprimir(rfEditado.getImprimir());
                    rolFuncionalidadRepository.save(rf);
                }
            }
        }

        auditoriaService.registrar("Seguridad", "rol", "EDITAR", guardado.getIdRol(), valorAnterior, "Nombre: " + guardado.getNombreRol());
        return guardado;
    }

    @Transactional
    public void eliminarRol(Integer id) {
        Rol rol = rolRepository.findByIdRolAndEstadoTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("El rol no existe."));

        // Validar si tiene usuarios asociados
        long countUsuarios = usuarioRepository.findAll().stream()
                .filter(u -> u.getEstado() && u.getRol().getIdRol().equals(id))
                .count();

        if (countUsuarios > 0) {
            throw new IllegalStateException("No se puede eliminar el rol porque tiene usuarios asignados.");
        }

        rol.setEstado(false);
        rolRepository.save(rol);

        auditoriaService.registrar("Seguridad", "rol", "ELIMINAR", id, "Nombre: " + rol.getNombreRol(), "Estado: Inactivo");
    }

    public List<RolFuncionalidad> obtenerPermisosRol(Integer idRol) {
        return rolFuncionalidadRepository.findByRolIdRol(idRol);
    }
}
