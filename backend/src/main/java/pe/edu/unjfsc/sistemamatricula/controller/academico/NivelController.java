package pe.edu.unjfsc.sistemamatricula.controller.academico;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Nivel;
import pe.edu.unjfsc.sistemamatricula.service.academico.NivelService;

import java.util.List;

@RestController
@RequestMapping("/api/niveles")
@RequiredArgsConstructor
public class NivelController {

    private final NivelService nivelService;

    @GetMapping
    public ResponseEntity<List<Nivel>> listarActivos() {
        return ResponseEntity.ok(nivelService.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Nivel> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(nivelService.obtenerPorId(id));
    }
}