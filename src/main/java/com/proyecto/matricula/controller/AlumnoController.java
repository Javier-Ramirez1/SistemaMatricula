package com.proyecto.matricula.controller;

import com.proyecto.matricula.entity.Alumno;
import com.proyecto.matricula.entity.Persona;
import com.proyecto.matricula.repository.AlumnoRepository;
import com.proyecto.matricula.repository.PersonaRepository;
import com.proyecto.matricula.repository.TipoDocumentoRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/alumnos")
public class AlumnoController {

    private final AlumnoRepository alumnoRepository;
    private final PersonaRepository personaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    public AlumnoController(AlumnoRepository alumnoRepository, PersonaRepository personaRepository, TipoDocumentoRepository tipoDocumentoRepository) {
        this.alumnoRepository = alumnoRepository;
        this.personaRepository = personaRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'DIRECTOR', 'SECRETARIA')")
    public String listarAlumnos(Model model) {
        List<Alumno> alumnos = alumnoRepository.findAllByEstadoTrue();
        model.addAttribute("alumnos", alumnos);
        return "alumnos/lista";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String nuevoAlumnoForm(Model model) {
        model.addAttribute("tiposDoc", tipoDocumentoRepository.findAll());
        model.addAttribute("alumno", new Alumno());
        return "alumnos/nuevo";
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String registrarAlumno(@RequestParam("codTipoDocumento") Integer codTipoDocumento,
                                  @RequestParam("numeroDocumento") String numeroDocumento,
                                  @RequestParam("nombres") String nombres,
                                  @RequestParam("apellidoPaterno") String apellidoPaterno,
                                  @RequestParam("apellidoMaterno") String apellidoMaterno,
                                  @RequestParam("fechaNacimiento") String fechaNacimiento,
                                  @RequestParam(value = "celular", required = false) String celular,
                                  @RequestParam(value = "correoElectronico", required = false) String correoElectronico,
                                  @RequestParam(value = "direccion", required = false) String direccion,
                                  @RequestParam(value = "nombreApoderado", required = false) String nombreApoderado,
                                  @RequestParam(value = "celularApoderado", required = false) String celularApoderado,
                                  Model model) {
        try {
            // Validar clave única por DNI
            if (personaRepository.findByTipoDocumentoCodTipoDocumentoAndNumeroDocumentoAndEstadoTrue(codTipoDocumento, numeroDocumento).isPresent()) {
                throw new IllegalArgumentException("Ya existe una persona registrada con ese número de documento.");
            }

            // Guardar Persona
            Persona persona = new Persona();
            persona.setTipoDocumento(tipoDocumentoRepository.findById(codTipoDocumento).orElseThrow());
            persona.setNumeroDocumento(numeroDocumento);
            persona.setNombres(nombres);
            persona.setApellidoPaterno(apellidoPaterno);
            persona.setApellidoMaterno(apellidoMaterno);
            persona.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
            persona.setCelular(celular);
            persona.setCorreoElectronico(correoElectronico);
            persona.setDireccion(direccion);
            persona.setEstado(true);
            persona = personaRepository.save(persona);

            // Guardar Alumno
            Alumno alumno = new Alumno();
            alumno.setPersona(persona);
            alumno.setNombreApoderado(nombreApoderado);
            alumno.setCelularApoderado(celularApoderado);
            alumno.setEstado(true);

            alumnoRepository.save(alumno);
            return "redirect:/alumnos?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("tiposDoc", tipoDocumentoRepository.findAll());
            return "alumnos/nuevo";
        }
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String editarAlumnoForm(@PathVariable("id") Integer id, Model model) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado."));
        model.addAttribute("tiposDoc", tipoDocumentoRepository.findAll());
        model.addAttribute("alumno", alumno);
        return "alumnos/editar";
    }

    @PostMapping("/editar/{id}")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String actualizarAlumno(@PathVariable("id") Integer id,
                                   @RequestParam("codTipoDocumento") Integer codTipoDocumento,
                                   @RequestParam("numeroDocumento") String numeroDocumento,
                                   @RequestParam("nombres") String nombres,
                                   @RequestParam("apellidoPaterno") String apellidoPaterno,
                                   @RequestParam("apellidoMaterno") String apellidoMaterno,
                                   @RequestParam("fechaNacimiento") String fechaNacimiento,
                                   @RequestParam(value = "celular", required = false) String celular,
                                   @RequestParam(value = "correoElectronico", required = false) String correoElectronico,
                                   @RequestParam(value = "direccion", required = false) String direccion,
                                   @RequestParam(value = "nombreApoderado", required = false) String nombreApoderado,
                                   @RequestParam(value = "celularApoderado", required = false) String celularApoderado,
                                   Model model) {
        try {
            Alumno alumno = alumnoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado."));
            Persona persona = alumno.getPersona();

            // Guardar Persona
            persona.setTipoDocumento(tipoDocumentoRepository.findById(codTipoDocumento).orElseThrow());
            persona.setNumeroDocumento(numeroDocumento);
            persona.setNombres(nombres);
            persona.setApellidoPaterno(apellidoPaterno);
            persona.setApellidoMaterno(apellidoMaterno);
            persona.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
            persona.setCelular(celular);
            persona.setCorreoElectronico(correoElectronico);
            persona.setDireccion(direccion);
            personaRepository.save(persona);

            // Guardar Alumno
            alumno.setNombreApoderado(nombreApoderado);
            alumno.setCelularApoderado(celularApoderado);
            alumnoRepository.save(alumno);

            return "redirect:/alumnos?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("tiposDoc", tipoDocumentoRepository.findAll());
            model.addAttribute("alumno", alumnoRepository.findById(id).orElseThrow());
            return "alumnos/editar";
        }
    }

    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String eliminarAlumno(@PathVariable("id") Integer id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado."));
        alumno.setEstado(false); // Borrado lógico
        alumnoRepository.save(alumno);
        return "redirect:/alumnos?success=true";
    }
}

