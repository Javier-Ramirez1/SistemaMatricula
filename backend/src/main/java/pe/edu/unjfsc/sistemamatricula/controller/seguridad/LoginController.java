package pe.edu.unjfsc.sistemamatricula.controller.seguridad;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.dto.seguridad.LoginRequest;
import pe.edu.unjfsc.sistemamatricula.dto.seguridad.LoginResponse;
import pe.edu.unjfsc.sistemamatricula.service.seguridad.LoginService;

/** Login con emisión de JWT + bitácora en login_log. Ruta pública (ver SecurityConfig). */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest httpRequest) {
        return ResponseEntity.ok(loginService.login(request, httpRequest));
    }
}