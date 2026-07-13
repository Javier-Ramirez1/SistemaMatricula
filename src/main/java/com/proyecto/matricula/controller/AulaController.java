package com.proyecto.matricula.controller;

import com.proyecto.matricula.entity.Aula;
import com.proyecto.matricula.repository.AnioAcademicoRepository;
import com.proyecto.matricula.repository.AulaRepository;
import com.proyecto.matricula.repository.GradoRepository;
import com.proyecto.matricula.repository.NivelRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/aulas")
public class AulaController {

    private final AulaRepository aulaRepository;
    private final AnioAcademicoRepository anioAcademicoRepository;
    private final NivelRepository nivelRepository;
    private final GradoRepository gradoRepository;

    public AulaController(AulaRepository aulaRepository, AnioAcademicoRepository anioAcademicoRepository,
                          NivelRepository nivelRepository, GradoRepository gradoRepository) {
        this.aulaRepository = aulaRepository;
        this.anioAcademicoRepository = anioAcademicoRepository;
        this.nivelRepository = nivelRepository;
        this.gradoRepository = gradoRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'DIRECTOR', 'SECRETARIA')")
    public String listarAulas(@RequestParam(value = "codNivel", required = false) Integer codNivel, Model model) {
        List<Aula> aulas;
        if (codNivel != null) {
            aulas = aulaRepository.findAllByNivelCodNivelAndEstadoTrue(codNivel);
            model.addAttribute("nivelSeleccionado", codNivel);
        } else {
            aulas = aulaRepository.findAllByEstadoTrue();
        }
        model.addAttribute("aulas", aulas);
        model.addAttribute("niveles", nivelRepository.findAll());
        return "aulas/lista";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String nuevoAulaForm(Model model) {
        model.addAttribute("anios", anioAcademicoRepository.findAll());
        model.addAttribute("niveles", nivelRepository.findAll());
        model.addAttribute("grados", gradoRepository.findAll());
        model.addAttribute("aula", new Aula());
        return "aulas/nuevo";
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String registrarAula(@RequestParam("codAnioAcademico") Integer codAnioAcademico,
                                @RequestParam("codNivel") Integer codNivel,
                                @RequestParam("codGrado") Integer codGrado,
                                @RequestParam("seccion") String seccion,
                                @RequestParam("capacidadMaxima") Short capacidadMaxima,
                                Model model) {
        try {
            if (aulaRepository.existsByAnioAcademicoCodAnioAcademicoAndNivelCodNivelAndGradoCodGradoAndSeccionAndEstadoTrue(
                    codAnioAcademico, codNivel, codGrado, seccion)) {
                throw new IllegalArgumentException("Ya existe esta sección de grado registrada para el año académico.");
            }

            Aula aula = new Aula();
            aula.setAnioAcademico(anioAcademicoRepository.findById(codAnioAcademico).orElseThrow());
            aula.setNivel(nivelRepository.findById(codNivel).orElseThrow());
            aula.setGrado(gradoRepository.findById(codGrado).orElseThrow());
            aula.setSeccion(seccion.toUpperCase());
            aula.setCapacidadMaxima(capacidadMaxima);
            aula.setVacantesDisponibles(capacidadMaxima);
            aula.setEstado(true);

            aulaRepository.save(aula);
            return "redirect:/aulas?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("anios", anioAcademicoRepository.findAll());
            model.addAttribute("niveles", nivelRepository.findAll());
            model.addAttribute("grados", gradoRepository.findAll());
            return "aulas/nuevo";
        }
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String editarAulaForm(@PathVariable("id") Integer id, Model model) {
        Aula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aula no encontrada."));
        model.addAttribute("anios", anioAcademicoRepository.findAll());
        model.addAttribute("niveles", nivelRepository.findAll());
        model.addAttribute("grados", gradoRepository.findAll());
        model.addAttribute("aula", aula);
        return "aulas/editar";
    }

    @PostMapping("/editar/{id}")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String actualizarAula(@PathVariable("id") Integer id,
                                 @RequestParam("codAnioAcademico") Integer codAnioAcademico,
                                 @RequestParam("codNivel") Integer codNivel,
                                 @RequestParam("codGrado") Integer codGrado,
                                 @RequestParam("seccion") String seccion,
                                 @RequestParam("capacidadMaxima") Short capacidadMaxima,
                                 Model model) {
        try {
            Aula aula = aulaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Aula no encontrada."));

            // Validar clave única si cambia sección/grado/nivel
            if (!(aula.getAnioAcademico().getCodAnioAcademico().equals(codAnioAcademico) &&
                    aula.getNivel().getCodNivel().equals(codNivel) &&
                    aula.getGrado().getCodGrado().equals(codGrado) &&
                    aula.getSeccion().equalsIgnoreCase(seccion)) &&
                    aulaRepository.existsByAnioAcademicoCodAnioAcademicoAndNivelCodNivelAndGradoCodGradoAndSeccionAndEstadoTrue(
                            codAnioAcademico, codNivel, codGrado, seccion)) {
                throw new IllegalArgumentException("Ya existe esta sección de grado registrada para el año académico.");
            }

            aula.setAnioAcademico(anioAcademicoRepository.findById(codAnioAcademico).orElseThrow());
            aula.setNivel(nivelRepository.findById(codNivel).orElseThrow());
            aula.setGrado(gradoRepository.findById(codGrado).orElseThrow());
            aula.setSeccion(seccion.toUpperCase());

            // Reajustar vacantes disponibles
            short diferencia = (short) (capacidadMaxima - aula.getCapacidadMaxima());
            aula.setCapacidadMaxima(capacidadMaxima);
            aula.setVacantesDisponibles((short) (aula.getVacantesDisponibles() + diferencia));

            aulaRepository.save(aula);
            return "redirect:/aulas?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("anios", anioAcademicoRepository.findAll());
            model.addAttribute("niveles", nivelRepository.findAll());
            model.addAttribute("grados", gradoRepository.findAll());
            model.addAttribute("aula", aulaRepository.findById(id).orElseThrow());
            return "aulas/editar";
        }
    }

    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String eliminarAula(@PathVariable("id") Integer id) {
        Aula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aula no encontrada."));
        aula.setEstado(false);
        aulaRepository.save(aula);
        return "redirect:/aulas?success=true";
    }
}
