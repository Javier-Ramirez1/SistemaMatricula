package com.proyecto.matricula.controller;

import com.proyecto.matricula.entity.Usuario;
import com.proyecto.matricula.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class PasswordController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @org.springframework.web.bind.annotation.GetMapping("/cambiar-password")
    public org.springframework.web.servlet.ModelAndView redirectHome() {
        return new org.springframework.web.servlet.ModelAndView("redirect:/");
    }

    @PostMapping("/api/usuarios/cambiar-password")
    public Map<String, Object> procesarCambiarPassword(@RequestParam("passwordActual") String passwordActual,
                                                       @RequestParam("passwordNueva") String passwordNueva,
                                                       @RequestParam("passwordConfirmacion") String passwordConfirmacion) {
        Map<String, Object> response = new HashMap<>();
        
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "Sesión no válida o expirada.");
            return response;
        }
        
        String username = auth.getName();
        Optional<Usuario> optUsuario = usuarioRepository.findByUsernameAndEstadoTrue(username);
        
        if (!optUsuario.isPresent()) {
            response.put("success", false);
            response.put("message", "Usuario no encontrado.");
            return response;
        }
        
        Usuario usuario = optUsuario.get();
        
        // Verificar contraseña actual
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            response.put("success", false);
            response.put("message", "La contraseña actual no es correcta.");
            return response;
        }
        
        // Validar nueva contraseña
        if (passwordNueva.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "La nueva contraseña no puede estar vacía.");
            return response;
        }
        
        if (!passwordNueva.equals(passwordConfirmacion)) {
            response.put("success", false);
            response.put("message", "Las contraseñas nuevas no coinciden.");
            return response;
        }
        
        // Encriptar y guardar
        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
        
        response.put("success", true);
        response.put("message", "Contraseña cambiada exitosamente.");
        return response;
    }
}
