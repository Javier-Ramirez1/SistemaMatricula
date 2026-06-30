package pe.edu.unjfsc.sistemamatricula.controller.financiero;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.MatricularRequest;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.MatriculaResponse;
import pe.edu.unjfsc.sistemamatricula.process.MatriculaProcess;
import pe.edu.unjfsc.sistemamatricula.service.financiero.MatriculaService;

import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;
    private final MatriculaProcess matriculaProcess;

    @GetMapping
    public ResponseEntity<List<MatriculaResponse>> listarPorAnio(@RequestParam Long idAnioAcademico) {
        return ResponseEntity.ok(matriculaService.listarPorAnio(idAnioAcademico));
    }

    @GetMapping("/buscar")
    public ResponseEntity<MatriculaResponse> obtenerPorAlumnoYAnio(@RequestParam Long idAlumno,
                                                                   @RequestParam Long idAnioAcademico) {
        return ResponseEntity.ok(matriculaService.obtenerPorAlumnoYAnio(idAlumno, idAnioAcademico));
    }

    /** Registra una nueva matrícula: valida cupo, genera la matrícula y sus cuotas. */
    @PostMapping
    @PreAuthorize("hasAuthority('FINANZAS_MATRICULA_PROCESAR')")
    public ResponseEntity<MatriculaResponse> matricular(@Valid @RequestBody MatricularRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matriculaProcess.matricular(request));
    }
}