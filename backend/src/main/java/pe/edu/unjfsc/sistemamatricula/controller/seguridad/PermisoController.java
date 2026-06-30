package pe.edu.unjfsc.sistemamatricula.controller.seguridad;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.dto.seguridad.PermisoResponse;
import pe.edu.unjfsc.sistemamatricula.service.seguridad.PermisoService;

import java.util.List;

/** Catálogo de permisos atómicos — alimenta el árbol de checkboxes del panel de Superusuario. */
@RestController
@RequestMapping("/api/permisos")
@RequiredArgsConstructor
public class PermisoController {

    private final PermisoService permisoService;

    @GetMapping
    public ResponseEntity<List<PermisoResponse>> listarTodos() {
        return ResponseEntity.ok(permisoService.listarTodos());
    }
}