package pe.edu.unjfsc.sistemamatricula.controller.seguridad;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.dto.seguridad.AsignarPermisosRequest;
import pe.edu.unjfsc.sistemamatricula.dto.seguridad.RolRequest;
import pe.edu.unjfsc.sistemamatricula.dto.seguridad.RolResponse;
import pe.edu.unjfsc.sistemamatricula.service.seguridad.RolService;

import java.util.List;

/** Gestión de roles y asignación de permisos (modal "Asignar Permisos" del boceto). */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    @GetMapping
    public ResponseEntity<List<RolResponse>> listarTodos() {
        return ResponseEntity.ok(rolService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<RolResponse> crear(@Valid @RequestBody RolRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolService.crear(request));
    }

    @PostMapping("/asignar-permisos")
    public ResponseEntity<RolResponse> asignarPermisos(@Valid @RequestBody AsignarPermisosRequest request) {
        return ResponseEntity.ok(rolService.asignarPermisos(request));
    }
}