 package pe.edu.unjfsc.sistemamatricula.controller.seguridad;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
        import pe.edu.unjfsc.sistemamatricula.dto.seguridad.CambiarPasswordRequest;
import pe.edu.unjfsc.sistemamatricula.service.seguridad.UsuarioService;

/** Permite a cualquier usuario autenticado cambiar su propia contraseña. */
@RestController
@RequestMapping("/api/usuarios/me")
@RequiredArgsConstructor
public class CambiarPasswordController {

    private final UsuarioService usuarioService;

    @PostMapping("/password")
    public ResponseEntity<Void> cambiarPassword(@Valid @RequestBody CambiarPasswordRequest request,
                                                Authentication authentication) {
        usuarioService.cambiarPassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }
}