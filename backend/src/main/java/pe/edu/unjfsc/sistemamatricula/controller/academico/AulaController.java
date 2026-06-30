package pe.edu.unjfsc.sistemamatricula.controller.academico;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.dto.academico.AulaRequest;
import pe.edu.unjfsc.sistemamatricula.dto.academico.AulaResponse;
import pe.edu.unjfsc.sistemamatricula.service.academico.AulaService;

import java.util.List;

/** Aula = año + nivel + grado + sección, con capacidad/vacantes (modal "Buscar Aula"). */
@RestController
@RequestMapping("/api/aulas")
@RequiredArgsConstructor
public class AulaController {

    private final AulaService aulaService;

    @GetMapping
    public ResponseEntity<List<AulaResponse>> listarPorAnio(@RequestParam Long idAnioAcademico) {
        return ResponseEntity.ok(aulaService.listarPorAnio(idAnioAcademico));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ACADEMICO_AULA_CREAR')")
    public ResponseEntity<AulaResponse> crear(@Valid @RequestBody AulaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(aulaService.crear(request));
    }
}