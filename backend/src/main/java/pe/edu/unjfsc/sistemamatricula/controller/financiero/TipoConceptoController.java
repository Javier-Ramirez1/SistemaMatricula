package pe.edu.unjfsc.sistemamatricula.controller.financiero;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.TipoConcepto;
import pe.edu.unjfsc.sistemamatricula.service.financiero.TipoConceptoService;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-concepto")
@RequiredArgsConstructor
public class TipoConceptoController {

    private final TipoConceptoService tipoConceptoService;

    @GetMapping
    public ResponseEntity<List<TipoConcepto>> listarActivos() {
        return ResponseEntity.ok(tipoConceptoService.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoConcepto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoConceptoService.obtenerPorId(id));
    }
}