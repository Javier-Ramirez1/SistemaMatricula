package com.proyecto.matricula.service;

import com.proyecto.matricula.entity.RolFuncionalidad;
import com.proyecto.matricula.entity.Usuario;
import com.proyecto.matricula.repository.RolFuncionalidadRepository;
import com.proyecto.matricula.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("securityService")
public class SecurityService {

    private final UsuarioRepository usuarioRepository;
    private final RolFuncionalidadRepository rolFuncionalidadRepository;

    public SecurityService(UsuarioRepository usuarioRepository, RolFuncionalidadRepository rolFuncionalidadRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolFuncionalidadRepository = rolFuncionalidadRepository;
    }

    public boolean tienePermiso(String funcionalidadNombre, String accion) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        String username = auth.getName();
        if (username == null || username.equalsIgnoreCase("anonymousUser")) {
            return false;
        }

        Optional<Usuario> optUsuario = usuarioRepository.findByUsernameAndEstadoTrue(username);
        if (!optUsuario.isPresent()) {
            return false;
        }

        Usuario usuario = optUsuario.get();
        // El SUPERUSUARIO siempre tiene acceso completo
        if (usuario.getRol().getNombreRol().equalsIgnoreCase("SUPERUSUARIO")) {
            return true;
        }

        List<RolFuncionalidad> permisos = rolFuncionalidadRepository.findByRolIdRol(usuario.getRol().getIdRol());
        for (RolFuncionalidad p : permisos) {
            if (p.getFuncionalidad().getNombre().equalsIgnoreCase(funcionalidadNombre)) {
                switch (accion.toUpperCase()) {
                    case "VER":
                        return p.getVer();
                    case "CREAR":
                        return p.getCrear();
                    case "EDITAR":
                        return p.getEditar();
                    case "ELIMINAR":
                        return p.getEliminar();
                    case "IMPRIMIR":
                        return p.getImprimir();
                }
            }
        }
        return false;
    }
}
