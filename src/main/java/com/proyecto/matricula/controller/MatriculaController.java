package com.proyecto.matricula.controller;

import com.proyecto.matricula.entity.Alumno;
import com.proyecto.matricula.entity.Aula;
import com.proyecto.matricula.entity.Matricula;
import com.proyecto.matricula.repository.AlumnoRepository;
import com.proyecto.matricula.repository.AulaRepository;
import com.proyecto.matricula.repository.MatriculaRepository;
import com.proyecto.matricula.service.MatriculaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;
    private final MatriculaRepository matriculaRepository;
    private final AlumnoRepository alumnoRepository;
    private final AulaRepository aulaRepository;
    private final com.proyecto.matricula.repository.NivelRepository nivelRepository;
    private final com.proyecto.matricula.repository.AnioAcademicoRepository anioAcademicoRepository;

    public MatriculaController(MatriculaService matriculaService, MatriculaRepository matriculaRepository,
                               AlumnoRepository alumnoRepository, AulaRepository aulaRepository,
                               com.proyecto.matricula.repository.NivelRepository nivelRepository,
                               com.proyecto.matricula.repository.AnioAcademicoRepository anioAcademicoRepository) {
        this.matriculaService = matriculaService;
        this.matriculaRepository = matriculaRepository;
        this.alumnoRepository = alumnoRepository;
        this.aulaRepository = aulaRepository;
        this.nivelRepository = nivelRepository;
        this.anioAcademicoRepository = anioAcademicoRepository;
    }

    @GetMapping
    @PreAuthorize("@securityService.tienePermiso('Matriculas', 'VER')")
    public String listarMatriculas(@RequestParam(value = "codAnioAcademico", required = false) Integer codAnioAcademico,
                                   @RequestParam(value = "codNivel", required = false) Integer codNivel,
                                   Model model) {
        List<Matricula> matriculas;
        if (codAnioAcademico != null && codNivel != null) {
            matriculas = matriculaRepository.findAllByAnioAcademicoCodAnioAcademicoAndAulaNivelCodNivelAndEstadoTrue(codAnioAcademico, codNivel);
            model.addAttribute("anioSeleccionado", codAnioAcademico);
            model.addAttribute("nivelSeleccionado", codNivel);
        } else if (codAnioAcademico != null) {
            matriculas = matriculaRepository.findAllByAnioAcademicoCodAnioAcademicoAndEstadoTrue(codAnioAcademico);
            model.addAttribute("anioSeleccionado", codAnioAcademico);
        } else if (codNivel != null) {
            matriculas = matriculaRepository.findAllByAulaNivelCodNivelAndEstadoTrue(codNivel);
            model.addAttribute("nivelSeleccionado", codNivel);
        } else {
            matriculas = matriculaRepository.findAllByEstadoTrue();
        }
        model.addAttribute("matriculas", matriculas);
        model.addAttribute("niveles", nivelRepository.findAll());
        model.addAttribute("anios", anioAcademicoRepository.findAll());
        return "matriculas/lista";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("@securityService.tienePermiso('Registrar Matricula', 'VER')")
    public String nuevaMatriculaForm(Model model) {
        List<Alumno> alumnos = alumnoRepository.findAllByEstadoTrue();
        List<Aula> aulas = aulaRepository.findAllByEstadoTrue();
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("aulas", aulas);
        return "matriculas/nuevo";
    }

    @PostMapping("/nuevo")
    @PreAuthorize("@securityService.tienePermiso('Registrar Matricula', 'CREAR')")
    public String registrarMatricula(@RequestParam("codAlumno") Integer codAlumno,
                                     @RequestParam("codAula") Integer codAula,
                                     Model model) {
        try {
            matriculaService.registrarMatricula(codAlumno, codAula);
            return "redirect:/matriculas?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("alumnos", alumnoRepository.findAllByEstadoTrue());
            model.addAttribute("aulas", aulaRepository.findAllByEstadoTrue());
            return "matriculas/nuevo";
        }
    }
}
