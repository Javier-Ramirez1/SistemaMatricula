package pe.edu.unjfsc.sistemamatricula.controller.reportes;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import pe.edu.unjfsc.sistemamatricula.dto.academico.AlumnoResponse;
import pe.edu.unjfsc.sistemamatricula.service.academico.AlumnoService;

import java.util.List;

/**
 * Listado de alumnos para fines de reporte/impresión. Reutiliza
 * AlumnoService porque el listado de alumnos activos ya es de
 * solo lectura; si más adelante se necesita un reporte agregado
 * propio (ej. alumnos por nivel/grado), se añade un método
 * específico en ReporteService.
 */
@RestController
@RequestMapping("/api/reportes/alumnos")
@RequiredArgsConstructor
public class ReporteAlumnoController {

    private final AlumnoService alumnoService;

    @GetMapping
    public ResponseEntity<List<AlumnoResponse>> listar() {
        return ResponseEntity.ok(alumnoService.listarTodos());
    }
}