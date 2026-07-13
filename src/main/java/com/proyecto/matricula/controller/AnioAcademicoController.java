package com.proyecto.matricula.controller;

import com.proyecto.matricula.entity.AnioAcademico;
import com.proyecto.matricula.repository.AnioAcademicoRepository;
import com.proyecto.matricula.service.ConceptoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/anios")
public class AnioAcademicoController {

    private final AnioAcademicoRepository anioAcademicoRepository;
    private final ConceptoService conceptoService;

    public AnioAcademicoController(AnioAcademicoRepository anioAcademicoRepository, ConceptoService conceptoService) {
        this.anioAcademicoRepository = anioAcademicoRepository;
        this.conceptoService = conceptoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'DIRECTOR', 'SECRETARIA')")
    public String listarAnios(Model model) {
        List<AnioAcademico> anios = anioAcademicoRepository.findAll();
        model.addAttribute("anios", anios);
        model.addAttribute("aniosActivos", anioAcademicoRepository.findAllByEstadoTrue());
        return "anios/lista";
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String crearAnio(@RequestParam("anio") Integer anio,
                            @RequestParam("descripcion") String descripcion,
                            @RequestParam(value = "duplicarConceptos", required = false) Boolean duplicarConceptos,
                            @RequestParam(value = "anioOrigen", required = false) Integer anioOrigen,
                            Model model) {
        try {
            // Validar si ya existe
            Optional<AnioAcademico> existente = anioAcademicoRepository.findByAnioAndEstadoTrue(anio);
            if (existente.isPresent()) {
                throw new IllegalArgumentException("El año académico " + anio + " ya se encuentra registrado y activo.");
            }

            AnioAcademico nuevoAnio = new AnioAcademico();
            nuevoAnio.setAnio(anio);
            nuevoAnio.setDescripcion(descripcion);
            nuevoAnio.setEstado(true);

            anioAcademicoRepository.save(nuevoAnio);

            // Clonación de conceptos si se solicita
            if (duplicarConceptos != null && duplicarConceptos && anioOrigen != null) {
                conceptoService.clonarConceptos(anioOrigen, anio);
            }

            return "redirect:/anios?success=Periodo+acad%C3%A9mico+creado+e+inicializado+correctamente.";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("anios", anioAcademicoRepository.findAll());
            model.addAttribute("aniosActivos", anioAcademicoRepository.findAllByEstadoTrue());
            return "anios/lista";
        }
    }

    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO')")
    public String desactivarAnio(@PathVariable("id") Integer id) {
        Optional<AnioAcademico> opt = anioAcademicoRepository.findById(id);
        if (opt.isPresent()) {
            AnioAcademico anio = opt.get();
            anio.setEstado(false);
            anioAcademicoRepository.save(anio);
        }
        return "redirect:/anios?success=Periodo+desactivado+correctamente.";
    }
}
