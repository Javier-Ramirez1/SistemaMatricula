package pe.edu.unjfsc.sistemamatricula.controller.academico;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.dto.academico.AnioAcademicoRequest;
import pe.edu.unjfsc.sistemamatricula.entity.academico.AnioAcademico;
import pe.edu.unjfsc.sistemamatricula.service.academico.AnioAcademicoService;

import java.util.List;

/** Año académico: pieza central de trazabilidad (ver comentario en el Service). */
@RestController
@RequestMapping("/api/anios-academicos")
@RequiredArgsConstructor
public class AnioAcademicoController {

    private final AnioAcademicoService anioAcademicoService;

    @GetMapping
    public ResponseEntity<List<AnioAcademico>> listarTodos() {
        return ResponseEntity.ok(anioAcademicoService.listarTodos());
    }

    @GetMapping("/activo")
    public ResponseEntity<AnioAcademico> obtenerActivo() {
        return ResponseEntity.ok(anioAcademicoService.obtenerActivo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnioAcademico> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(anioAcademicoService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ACADEMICO_ANIOACADEMICO_CREAR')")
    public ResponseEntity<AnioAcademico> crear(@Valid @RequestBody AnioAcademicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(anioAcademicoService.crear(request));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('ACADEMICO_ANIOACADEMICO_ACTUALIZAR')")
    public ResponseEntity<AnioAcademico> activar(@PathVariable Long id) {
        return ResponseEntity.ok(anioAcademicoService.activar(id));
    }
}