package pe.edu.unjfsc.sistemamatricula.controller.academico;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Grado;
import pe.edu.unjfsc.sistemamatricula.service.academico.GradoService;

import java.util.List;

@RestController
@RequestMapping("/api/grados")
@RequiredArgsConstructor
public class GradoController {

    private final GradoService gradoService;

    @GetMapping
    public ResponseEntity<List<Grado>> listarPorNivel(@RequestParam Long idNivel) {
        return ResponseEntity.ok(gradoService.listarPorNivel(idNivel));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grado> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(gradoService.obtenerPorId(id));
    }
}