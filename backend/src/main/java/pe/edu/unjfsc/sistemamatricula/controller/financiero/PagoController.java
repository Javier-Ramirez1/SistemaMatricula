package pe.edu.unjfsc.sistemamatricula.controller.financiero;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.PagarCuotaRequest;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.ReciboResponse;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Cuota;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Pago;
import pe.edu.unjfsc.sistemamatricula.process.PagoProcess;
import pe.edu.unjfsc.sistemamatricula.service.financiero.PagoService;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;
    private final PagoProcess pagoProcess;

    @GetMapping("/deudas/{idMatricula}")
    public ResponseEntity<List<Cuota>> listarDeudasPorMatricula(@PathVariable Long idMatricula) {
        return ResponseEntity.ok(pagoService.listarDeudasPorMatricula(idMatricula));
    }

    @GetMapping("/deudas/{idMatricula}/pendientes")
    public ResponseEntity<List<Cuota>> listarDeudasPendientes(@PathVariable Long idMatricula) {
        return ResponseEntity.ok(pagoService.listarDeudasPendientes(idMatricula));
    }

    @GetMapping("/historial/{idMatricula}")
    public ResponseEntity<List<Pago>> listarHistorialPorMatricula(@PathVariable Long idMatricula) {
        return ResponseEntity.ok(pagoService.listarHistorialPorMatricula(idMatricula));
    }

    @GetMapping("/cuotas/{id}")
    public ResponseEntity<Cuota> obtenerCuotaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.obtenerCuotaPorId(id));
    }

    /** Registra el pago de una cuota: valida orden de cobro, emite Pago + Recibo con boleta correlativa. */
    @PostMapping
    @PreAuthorize("hasAuthority('FINANZAS_PAGO_PROCESAR')")
    public ResponseEntity<ReciboResponse> pagar(@Valid @RequestBody PagarCuotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoProcess.pagarCuota(request));
    }
}