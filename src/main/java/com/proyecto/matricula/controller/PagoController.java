package com.proyecto.matricula.controller;

import com.proyecto.matricula.entity.Alumno;
import com.proyecto.matricula.entity.Cuota;
import com.proyecto.matricula.entity.Pago;
import com.proyecto.matricula.repository.AlumnoRepository;
import com.proyecto.matricula.repository.CuotaRepository;
import com.proyecto.matricula.service.PagoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService pagoService;
    private final CuotaRepository cuotaRepository;
    private final AlumnoRepository alumnoRepository;
    private final com.proyecto.matricula.repository.PagoRepository pagoRepository;

    public PagoController(PagoService pagoService, CuotaRepository cuotaRepository,
                          AlumnoRepository alumnoRepository, com.proyecto.matricula.repository.PagoRepository pagoRepository) {
        this.pagoService = pagoService;
        this.cuotaRepository = cuotaRepository;
        this.alumnoRepository = alumnoRepository;
        this.pagoRepository = pagoRepository;
    }

    @GetMapping("/deudas")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'DIRECTOR', 'SECRETARIA')")
    public String consultarDeudas(@RequestParam(value = "codAlumno", required = false) Integer codAlumno, Model model) {
        List<Alumno> alumnos = alumnoRepository.findAllByEstadoTrue();
        model.addAttribute("alumnos", alumnos);

        if (codAlumno != null) {
            List<Cuota> deudas = cuotaRepository.findAllPendingCuotasByAlumno(codAlumno);
            model.addAttribute("deudas", deudas);
            
            List<Pago> historial = pagoRepository.findAllByCuotaMatriculaAlumnoCodAlumnoAndEstadoTrueOrderByFechaPagoDesc(codAlumno);
            model.addAttribute("historial", historial);
            
            model.addAttribute("alumnoSeleccionado", codAlumno);
        }
        return "pagos/deudas";
    }

    @PostMapping("/registrar")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String registrarPago(@RequestParam("codCuota") Integer codCuota,
                                @RequestParam("montoPagado") BigDecimal montoPagado,
                                Model model) {
        try {
            pagoService.registrarPago(codCuota, montoPagado);
            return "redirect:/pagos/deudas?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/pagos/deudas?error=" + e.getMessage();
        }
    }
}
