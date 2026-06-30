package pe.edu.unjfsc.sistemamatricula.controller.reportes;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.dto.reportes.DashboardDirectorResponse;
import pe.edu.unjfsc.sistemamatricula.service.reportes.ReporteService;

/** Dashboard "5. DIRECTOR - SOLO CONSULTA": tarjetas + gráficos por año académico. */
@RestController
@RequestMapping("/api/reportes/matriculas")
@RequiredArgsConstructor
public class ReporteMatriculaController {

    private final ReporteService reporteService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDirectorResponse> obtenerDashboard(@RequestParam Long idAnioAcademico) {
        return ResponseEntity.ok(reporteService.obtenerDashboard(idAnioAcademico));
    }
}