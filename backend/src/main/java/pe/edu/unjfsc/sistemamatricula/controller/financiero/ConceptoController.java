package pe.edu.unjfsc.sistemamatricula.controller.financiero;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.ClonarConceptosRequest;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.ConceptoRequest;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.ConceptoResponse;
import pe.edu.unjfsc.sistemamatricula.service.financiero.ConceptoService;

import java.util.List;

/** Tarifario por año académico, con herramienta de clonado entre años. */
@RestController
@RequestMapping("/api/conceptos")
@RequiredArgsConstructor
public class ConceptoController {

    private final ConceptoService conceptoService;

    @GetMapping
    public ResponseEntity<List<ConceptoResponse>> listarPorAnio(@RequestParam Long idAnioAcademico) {
        return ResponseEntity.ok(conceptoService.listarPorAnio(idAnioAcademico));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FINANZAS_CONCEPTO_CREAR')")
    public ResponseEntity<ConceptoResponse> crear(@Valid @RequestBody ConceptoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conceptoService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANZAS_CONCEPTO_ACTUALIZAR')")
    public ResponseEntity<ConceptoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ConceptoRequest request) {
        return ResponseEntity.ok(conceptoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANZAS_CONCEPTO_ELIMINAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        conceptoService.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/clonar")
    @PreAuthorize("hasAuthority('FINANZAS_CONCEPTO_CREAR')")
    public ResponseEntity<List<ConceptoResponse>> clonar(@Valid @RequestBody ClonarConceptosRequest request) {
        return ResponseEntity.ok(conceptoService.clonarConceptos(request));
    }
}
