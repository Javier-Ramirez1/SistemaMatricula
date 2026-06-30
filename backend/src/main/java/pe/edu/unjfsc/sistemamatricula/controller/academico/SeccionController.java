package pe.edu.unjfsc.sistemamatricula.controller.academico;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Seccion;
import pe.edu.unjfsc.sistemamatricula.service.academico.SeccionService;

import java.util.List;

@RestController
@RequestMapping("/api/secciones")
@RequiredArgsConstructor
public class SeccionController {

    private final SeccionService seccionService;

    @GetMapping
    public ResponseEntity<List<Seccion>> listarActivas() {
        return ResponseEntity.ok(seccionService.listarActivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seccion> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(seccionService.obtenerPorId(id));
    }
}