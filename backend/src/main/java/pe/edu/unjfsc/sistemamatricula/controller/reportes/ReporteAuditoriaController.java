package pe.edu.unjfsc.sistemamatricula.controller.reportes;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.dto.reportes.AuditoriaResponse;
import pe.edu.unjfsc.sistemamatricula.service.reportes.ReporteService;

import java.util.List;

/** Consulta de la bitácora de auditoría (quién hizo qué, cuándo y desde dónde). */
@RestController
@RequestMapping("/api/reportes/auditoria")
@RequiredArgsConstructor
public class ReporteAuditoriaController {

    private final ReporteService reporteService;

    @GetMapping("/reciente")
    public ResponseEntity<List<AuditoriaResponse>> listarReciente() {
        return ResponseEntity.ok(reporteService.listarAuditoriaReciente());
    }

    @GetMapping
    public ResponseEntity<List<AuditoriaResponse>> listarPorRegistro(@RequestParam String tabla,
                                                                     @RequestParam Long idRegistro) {
        return ResponseEntity.ok(reporteService.listarAuditoriaPorRegistro(tabla, idRegistro));
    }
}