package pe.edu.unjfsc.sistemamatricula.service.reportes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.dto.reportes.*;
import pe.edu.unjfsc.sistemamatricula.entity.academico.AnioAcademico;
import pe.edu.unjfsc.sistemamatricula.entity.auditoria.Auditoria;
import pe.edu.unjfsc.sistemamatricula.entity.enums.EstadoMatricula;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Recibo;
import pe.edu.unjfsc.sistemamatricula.repository.*;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Agregaciones para la "5. DIRECTOR - SOLO CONSULTA" del boceto:
 * tarjetas (aulas, alumnos matriculados, matrículas, ingresos del
 * mes), gráfico de barras por nivel y gráfico de línea de ingresos
 * mensuales. Todo de SOLO LECTURA, coherente con el rol DIRECTOR
 * que únicamente puede ver registros y reportes (nunca escribir).
 */
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final AulaRepository aulaRepository;
    private final MatriculaRepository matriculaRepository;
    private final ReciboRepository reciboRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final AnioAcademicoRepository anioAcademicoRepository;

    private static final String[] NOMBRES_MES = {
            "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
    };

    @Transactional(readOnly = true)
    public DashboardDirectorResponse obtenerDashboard(Long idAnioAcademico) {
        AnioAcademico anio = anioAcademicoRepository.findById(idAnioAcademico)
                .orElseThrow(() -> new pe.edu.unjfsc.sistemamatricula.exception.ResourceNotFoundException("AnioAcademico", idAnioAcademico));

        int totalAulas = (int) aulaRepository.countByAnioAcademicoIdAndEstado(idAnioAcademico, Constantes.ACTIVO);

        long totalMatriculas = matriculaRepository.countByAnioAcademicoIdAndEstadoAndEstadoMatricula(
                idAnioAcademico, Constantes.ACTIVO, EstadoMatricula.ACTIVA);

        // En este modelo, alumnos matriculados == matrículas activas del año
        // (un alumno tiene como máximo una matrícula activa por año académico).
        int totalAlumnosMatriculados = (int) totalMatriculas;

        BigDecimal ingresosDelMes = calcularIngresosMesActual();

        Map<String, Integer> matriculasPorNivel = construirMapaMatriculasPorNivel(idAnioAcademico);

        List<PuntoIngresoMensual> resumenIngresos = construirResumenIngresosMensuales(anio.getAnio());

        return new DashboardDirectorResponse(
                totalAulas,
                totalAlumnosMatriculados,
                (int) totalMatriculas,
                ingresosDelMes,
                matriculasPorNivel,
                resumenIngresos
        );
    }

    private BigDecimal calcularIngresosMesActual() {
        YearMonth mesActual = YearMonth.now();
        LocalDateTime desde = mesActual.atDay(1).atStartOfDay();
        LocalDateTime hasta = mesActual.plusMonths(1).atDay(1).atStartOfDay();
        BigDecimal total = reciboRepository.sumarMontoEntreFechas(desde, hasta);
        return total == null ? BigDecimal.ZERO : total;
    }

    private Map<String, Integer> construirMapaMatriculasPorNivel(Long idAnioAcademico) {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        for (Object[] fila : matriculaRepository.contarMatriculasPorNivel(idAnioAcademico)) {
            String nivel = (String) fila[0];
            Long cantidad = (Long) fila[1];
            mapa.put(nivel, cantidad.intValue());
        }
        return mapa;
    }

    /** Devuelve los 12 meses del año, con 0 en los meses sin ingresos (para que el gráfico no tenga huecos). */
    private List<PuntoIngresoMensual> construirResumenIngresosMensuales(int anio) {
        Map<Integer, BigDecimal> porMes = new LinkedHashMap<>();
        for (int mes = 1; mes <= 12; mes++) {
            porMes.put(mes, BigDecimal.ZERO);
        }
        for (Object[] fila : reciboRepository.sumarMontoPorMes(anio)) {
            int mes = ((Number) fila[0]).intValue();
            BigDecimal total = (BigDecimal) fila[1];
            porMes.put(mes, total);
        }
        return porMes.entrySet().stream()
                .map(e -> new PuntoIngresoMensual(NOMBRES_MES[e.getKey() - 1], e.getValue()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> listarAuditoriaReciente() {
        return auditoriaRepository.findTop100ByOrderByFechaDesc().stream()
                .map(this::toAuditoriaResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> listarAuditoriaPorRegistro(String tabla, Long idRegistro) {
        return auditoriaRepository.findByTablaAndIdRegistroOrderByFechaDesc(tabla, idRegistro).stream()
                .map(this::toAuditoriaResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportePagoResponse> listarPagosEntreFechas(LocalDateTime desde, LocalDateTime hasta) {
        return reciboRepository.findByFechaEmisionBetweenOrderByFechaEmisionDesc(desde, hasta).stream()
                .map(this::toReportePagoResponse)
                .collect(Collectors.toList());
    }

    private AuditoriaResponse toAuditoriaResponse(Auditoria a) {
        return new AuditoriaResponse(
                a.getId(), a.getTabla(), a.getIdRegistro(), a.getAccion(),
                a.getUsuario(), a.getIpCliente(), a.getObservacion(), a.getFecha()
        );
    }

    private ReportePagoResponse toReportePagoResponse(Recibo r) {
        return new ReportePagoResponse(
                r.getNumeroBoleta(),
                r.getMatricula().getAlumno().getNombreCompleto(),
                r.getPago().getCuota().getConcepto().getNombre(),
                r.getMontoPagado(),
                r.getPago().getTipoPago().name(),
                r.getFechaEmision(),
                r.getUsuarioInsert()
        );
    }
}