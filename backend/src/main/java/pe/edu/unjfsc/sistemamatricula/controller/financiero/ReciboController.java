package pe.edu.unjfsc.sistemamatricula.controller.financiero;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.ReciboResponse;
import pe.edu.unjfsc.sistemamatricula.service.financiero.ReciboService;

import java.util.List;

/** Consulta de recibos ya emitidos (la emisión ocurre dentro de PagoProcess). */
@RestController
@RequestMapping("/api/recibos")
@RequiredArgsConstructor
public class ReciboController {

    private final ReciboService reciboService;

    @GetMapping
    public ResponseEntity<List<ReciboResponse>> listarPorMatricula(@RequestParam Long idMatricula) {
        return ResponseEntity.ok(reciboService.listarPorMatricula(idMatricula));
    }

    @GetMapping("/boleta/{numeroBoleta}")
    public ResponseEntity<ReciboResponse> obtenerPorNumeroBoleta(@PathVariable String numeroBoleta) {
        return ResponseEntity.ok(reciboService.obtenerPorNumeroBoleta(numeroBoleta));
    }
}