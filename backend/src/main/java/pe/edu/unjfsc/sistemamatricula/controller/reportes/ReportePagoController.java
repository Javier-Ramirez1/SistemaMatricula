package pe.edu.unjfsc.sistemamatricula.controller.reportes;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.dto.reportes.ReportePagoResponse;
import pe.edu.unjfsc.sistemamatricula.service.reportes.ReporteService;

import java.time.LocalDateTime;
import java.util.List;

/** Reporte de pagos/recibos emitidos entre fechas, para cuadre de caja. */
@RestController
@RequestMapping("/api/reportes/pagos")
@RequiredArgsConstructor
public class ReportePagoController {

    private final ReporteService reporteService;

    @GetMapping
    public ResponseEntity<List<ReportePagoResponse>> listarEntreFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(reporteService.listarPagosEntreFechas(desde, hasta));
    }
}