package com.proyecto.matricula.controller;

import com.proyecto.matricula.repository.CuotaRepository;
import com.proyecto.matricula.repository.MatriculaRepository;
import com.proyecto.matricula.repository.PagoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class HomeController {

    private final MatriculaRepository matriculaRepository;
    private final PagoRepository pagoRepository;
    private final CuotaRepository cuotaRepository;

    public HomeController(MatriculaRepository matriculaRepository,
                          PagoRepository pagoRepository,
                          CuotaRepository cuotaRepository) {
        this.matriculaRepository = matriculaRepository;
        this.pagoRepository = pagoRepository;
        this.cuotaRepository = cuotaRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        int anioActual = LocalDate.now().getYear();

        long totalMatriculas = matriculaRepository.countByAnioAcademicoAnioAndEstadoTrue(anioActual);

        BigDecimal recaudadoMatriculas = pagoRepository.sumMontoPagadoByTipoConceptoAndAnio("Matricula", anioActual);
        long comprobantesEmitidos = pagoRepository.countByTipoConceptoAndAnio("Matricula", anioActual);

        BigDecimal cuentasPorCobrar = cuotaRepository.sumMontoPendienteByAnio(anioActual);
        long cuotasPendientes = cuotaRepository.countPendientesByAnio(anioActual);

        model.addAttribute("anioActual", anioActual);
        model.addAttribute("totalMatriculas", totalMatriculas);
        model.addAttribute("porcentajeMatriculados", 100); // ajusta si tienes una meta/capacidad total
        model.addAttribute("recaudadoMatriculas", recaudadoMatriculas);
        model.addAttribute("comprobantesEmitidos", comprobantesEmitidos);
        model.addAttribute("cuentasPorCobrar", cuentasPorCobrar);
        model.addAttribute("cuotasPendientes", cuotasPendientes);

        return "index";
    }

    @GetMapping("/403")
    public String accesoDenegado() {
        return "error/403";
    }
}