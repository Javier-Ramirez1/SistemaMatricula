package pe.edu.unjfsc.sistemamatricula.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** DTOs de salida para los dashboards/reportes (vista Director, vista Superusuario). */
public class reportes {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DashboardDirectorResponse {
        private int totalAulas;
        private int totalAlumnosMatriculados;
        private int totalMatriculasAnio;
        private BigDecimal ingresosDelMes;
        private Map<String, Integer> matriculasPorNivel;
        private List<PuntoIngresoMensual> resumenIngresosMensuales;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PuntoIngresoMensual {
        private String mes;
        private BigDecimal monto;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuditoriaResponse {
        private Long id;
        private String tabla;
        private Long idRegistro;
        private String accion;
        private String usuario;
        private String ipCliente;
        private String observacion;
        private LocalDateTime fecha;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReportePagoResponse {
        private String numeroBoleta;
        private String alumno;
        private String concepto;
        private BigDecimal monto;
        private String formaPago;
        private LocalDateTime fecha;
        private String registradoPor;
    }
}