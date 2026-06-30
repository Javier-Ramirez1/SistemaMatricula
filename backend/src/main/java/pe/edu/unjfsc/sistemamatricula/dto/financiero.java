package pe.edu.unjfsc.sistemamatricula.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.unjfsc.sistemamatricula.entity.enums.TipoPago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** DTOs del módulo financiero: concepto, matrícula, cuota, pago, recibo. */
public class financiero {

    @Data
    public static class ConceptoRequest {
        @NotNull(message = "El año académico es obligatorio")
        private Long idAnioAcademico;

        @NotNull(message = "El tipo de concepto es obligatorio")
        private Long idTipoConcepto;

        @NotBlank(message = "El nombre del concepto es obligatorio")
        private String nombre;

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
        private BigDecimal monto;

        @NotNull(message = "El orden de cobro es obligatorio")
        @Min(value = 1, message = "El orden de cobro debe ser mayor o igual a 1")
        private Integer ordenCobro;

        private boolean esDefault;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConceptoResponse {
        private Long id;
        private Integer anio;
        private String tipoConcepto;
        private String nombre;
        private BigDecimal monto;
        private Integer ordenCobro;
        private boolean esDefault;
        private Byte estado;
        private Long version;
    }

    /** Petición para clonar el tarifario de un año a otro (herramienta de clonado mencionada en los apuntes). */
    @Data
    public static class ClonarConceptosRequest {
        @NotNull(message = "El año académico origen es obligatorio")
        private Long idAnioAcademicoOrigen;

        @NotNull(message = "El año académico destino es obligatorio")
        private Long idAnioAcademicoDestino;
    }

    @Data
    public static class MatricularRequest {
        @NotNull(message = "El alumno es obligatorio")
        private Long idAlumno;

        @NotNull(message = "El aula es obligatoria")
        private Long idAula;

        @NotNull(message = "El año académico es obligatorio")
        private Long idAnioAcademico;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MatriculaResponse {
        private Long id;
        private String alumno;
        private String aula;
        private Integer anio;
        private LocalDate fechaMatricula;
        private String estadoMatricula;
        private Long version;
        private List<CuotaResponse> cuotas;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CuotaResponse {
        private Long id;
        private String concepto;
        private BigDecimal monto;
        private Integer ordenCobro;
        private String estadoPago;
        private boolean habilitadoParaPagar;
    }

    @Data
    public static class PagarCuotaRequest {
        @NotNull(message = "La cuota es obligatoria")
        private Long idCuota;

        @NotNull(message = "El tipo de pago es obligatorio")
        private TipoPago tipoPago;

        private String referenciaPago;

        /**
         * Version de la cuota recibida por el frontend al cargar la lista
         * de deudas — se usa para detectar conflictos de optimistic lock
         * explícitamente además del @Version automático de JPA.
         */
        private Long versionCuota;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReciboResponse {
        private Long id;
        private String numeroBoleta;
        private String alumno;
        private String concepto;
        private BigDecimal monto;
        private LocalDateTime fechaEmision;
        private String formaPago;
        private String recibidoPor;
    }
}