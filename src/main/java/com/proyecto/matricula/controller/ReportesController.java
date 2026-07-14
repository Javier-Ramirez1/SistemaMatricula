package com.proyecto.matricula.controller;

import com.proyecto.matricula.entity.Alumno;
import com.proyecto.matricula.entity.AnioAcademico;
import com.proyecto.matricula.entity.Auditoria;
import com.proyecto.matricula.entity.Pago;
import com.proyecto.matricula.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ReportesController {

    private final MatriculaRepository matriculaRepository;
    private final PagoRepository pagoRepository;
    private final AulaRepository aulaRepository;
    private final AlumnoRepository alumnoRepository;
    private final AnioAcademicoRepository anioAcademicoRepository;
    private final AuditoriaRepository auditoriaRepository;

    public ReportesController(MatriculaRepository matriculaRepository,
                              PagoRepository pagoRepository,
                              AulaRepository aulaRepository,
                              AlumnoRepository alumnoRepository,
                              AnioAcademicoRepository anioAcademicoRepository,
                              AuditoriaRepository auditoriaRepository) {
        this.matriculaRepository = matriculaRepository;
        this.pagoRepository = pagoRepository;
        this.aulaRepository = aulaRepository;
        this.alumnoRepository = alumnoRepository;
        this.anioAcademicoRepository = anioAcademicoRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @GetMapping("/reportes/dashboard")
    public String dashboard(Model model) {
        int anioActual = LocalDate.now().getYear();
        int mesActual = LocalDate.now().getMonthValue();

        // 1. KPIs
        long totalAulas = aulaRepository.countByEstadoTrue();
        long totalMatriculados = matriculaRepository.countByAnioAcademicoAnioAndEstadoTrue(anioActual);
        long totalMatriculas = matriculaRepository.countByAnioAcademicoAnioAndEstadoTrue(anioActual);

        BigDecimal ingresosMes = pagoRepository.sumMontoPagadoByMonthAndYear(mesActual, anioActual);
        if (ingresosMes == null) {
            ingresosMes = BigDecimal.ZERO;
        }

        model.addAttribute("totalAulas", totalAulas);
        model.addAttribute("totalMatriculados", totalMatriculados);
        model.addAttribute("totalMatriculas", totalMatriculas);
        model.addAttribute("ingresosMes", ingresosMes);
        model.addAttribute("anioActual", anioActual);

        // 2. Gráfico: Matrículas por Nivel
        long inicial = matriculaRepository.countByAnioAcademicoAnioAndAulaNivelNombreNivelAndEstadoTrue(anioActual, "Inicial");
        if (inicial == 0) {
            inicial = matriculaRepository.countByAnioAcademicoAnioAndAulaNivelNombreNivelAndEstadoTrue(anioActual, "INICIAL");
        }
        long primaria = matriculaRepository.countByAnioAcademicoAnioAndAulaNivelNombreNivelAndEstadoTrue(anioActual, "Primaria");
        if (primaria == 0) {
            primaria = matriculaRepository.countByAnioAcademicoAnioAndAulaNivelNombreNivelAndEstadoTrue(anioActual, "PRIMARIA");
        }
        long secundaria = matriculaRepository.countByAnioAcademicoAnioAndAulaNivelNombreNivelAndEstadoTrue(anioActual, "Secundaria");
        if (secundaria == 0) {
            secundaria = matriculaRepository.countByAnioAcademicoAnioAndAulaNivelNombreNivelAndEstadoTrue(anioActual, "SECUNDARIA");
        }

        model.addAttribute("nivelInicial", inicial);
        model.addAttribute("nivelPrimaria", primaria);
        model.addAttribute("nivelSecundaria", secundaria);

        // 3. Gráfico: Recaudación Mensual (Historial de Ingresos)
        List<Object[]> recaudacionMesRaw = pagoRepository.sumMontoPagadoByMonth(anioActual);
        Map<Integer, BigDecimal> mapRecaudacion = new HashMap<>();
        for (Object[] row : recaudacionMesRaw) {
            Integer mes = (Integer) row[0];
            BigDecimal total = (BigDecimal) row[1];
            mapRecaudacion.put(mes, total);
        }

        List<BigDecimal> recaudacionMensual = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            recaudacionMensual.add(mapRecaudacion.getOrDefault(m, BigDecimal.ZERO));
        }
        model.addAttribute("recaudacionMensual", recaudacionMensual);

        // 4. Historial de Pagos por Alumno
        List<Alumno> alumnos = alumnoRepository.findAllByEstadoTrue();
        model.addAttribute("alumnos", alumnos);

        List<AnioAcademico> anios = anioAcademicoRepository.findAllByEstadoTrue();
        model.addAttribute("anios", anios);

        // 5. Auditoría
        List<Auditoria> auditorias = auditoriaRepository.findTop50ByOrderByFechaHoraDesc();
        model.addAttribute("auditorias", auditorias);

        return "reportes/dashboard";
    }

    @GetMapping("/api/reportes/pagos-alumno")
    @ResponseBody
    public List<Map<String, Object>> getPagosAlumno(@RequestParam("codAlumno") Integer codAlumno,
                                                    @RequestParam(value = "anio", required = false) Integer anio) {
        List<Pago> pagos = pagoRepository.findAllByCuotaMatriculaAlumnoCodAlumnoAndEstadoTrueOrderByFechaPagoDesc(codAlumno);

        return pagos.stream()
                .filter(p -> anio == null || p.getCuota().getMatricula().getAnioAcademico().getAnio().equals(anio))
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("numeroBoleta", p.getSerieComprobante() + "-" + String.format("%06d", p.getNumeroComprobante()));
                    map.put("fechaPago", p.getFechaPago().toLocalDate().toString());
                    map.put("anio", p.getCuota().getMatricula().getAnioAcademico().getAnio());
                    map.put("concepto", p.getCuota().getConcepto().getNombreConcepto());
                    map.put("monto", p.getMontoPagado());
                    map.put("formaPago", "Efectivo"); // Por defecto
                    map.put("recibidoPor", p.getCuota().getUsuarioRegistro() != null ? "Secretaría" : "Sistema");
                    return map;
                })
                .collect(Collectors.toList());
    }
}
