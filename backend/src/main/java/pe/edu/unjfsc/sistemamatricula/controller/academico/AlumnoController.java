package pe.edu.unjfsc.sistemamatricula.controller.academico;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.dto.academico.AlumnoRequest;
import pe.edu.unjfsc.sistemamatricula.dto.academico.AlumnoResponse;
import pe.edu.unjfsc.sistemamatricula.service.academico.AlumnoService;

import java.util.List;

/** Mantenimiento de alumnos — modal "Buscar Alumno" del boceto. */
@RestController
@RequestMapping("/api/alumnos")
@RequiredArgsConstructor
public class AlumnoController {

    private final AlumnoService alumnoService;

    @GetMapping
    public ResponseEntity<List<AlumnoResponse>> listarTodos() {
        return ResponseEntity.ok(alumnoService.listarTodos());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<AlumnoResponse>> buscar(@RequestParam String texto) {
        return ResponseEntity.ok(alumnoService.buscarPorNombreOApellido(texto));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ACADEMICO_ALUMNO_CREAR')")
    public ResponseEntity<AlumnoResponse> crear(@Valid @RequestBody AlumnoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alumnoService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMICO_ALUMNO_ACTUALIZAR')")
    public ResponseEntity<AlumnoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody AlumnoRequest request) {
        return ResponseEntity.ok(alumnoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ACADEMICO_ALUMNO_ELIMINAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alumnoService.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }
}