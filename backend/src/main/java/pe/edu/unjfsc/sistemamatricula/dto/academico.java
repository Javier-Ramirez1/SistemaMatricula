package pe.edu.unjfsc.sistemamatricula.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** DTOs del módulo académico: año, nivel, grado, sección, aula, alumno. */
public class academico {

    @Data
    public static class AnioAcademicoRequest {
        @NotNull(message = "El año es obligatorio")
        @Min(value = 2020, message = "Año fuera de rango")
        @Max(value = 2100, message = "Año fuera de rango")
        private Integer anio;
    }

    @Data
    public static class AulaRequest {
        @NotNull(message = "El año académico es obligatorio")
        private Long idAnioAcademico;

        @NotNull(message = "El nivel es obligatorio")
        private Long idNivel;

        @NotNull(message = "El grado es obligatorio")
        private Long idGrado;

        @NotNull(message = "La sección es obligatoria")
        private Long idSeccion;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AulaResponse {
        private Long id;
        private Integer anio;
        private String nivel;
        private String grado;
        private String seccion;
        private Integer cantidadAlumnos;
        private Integer capacidadMaxima;
        private Integer vacantesDisponibles;
        private Byte estado;
        private Long version;
    }

    @Data
    public static class AlumnoRequest {
        @NotNull(message = "El tipo de documento es obligatorio")
        private Long idTipoDocumento;

        @NotBlank(message = "El número de documento es obligatorio")
        @Pattern(regexp = "^[0-9A-Za-z]{6,20}$", message = "Número de documento inválido")
        private String numeroDocumento;

        @NotBlank(message = "El apellido paterno es obligatorio")
        @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]+$", message = "Solo se permiten letras")
        private String apellidoPaterno;

        @NotBlank(message = "El apellido materno es obligatorio")
        @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]+$", message = "Solo se permiten letras")
        private String apellidoMaterno;

        @NotBlank(message = "Los nombres son obligatorios")
        @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]+$", message = "Solo se permiten letras")
        private String nombres;

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
        private LocalDate fechaNacimiento;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlumnoResponse {
        private Long id;
        private String tipoDocumento;
        private String numeroDocumento;
        private String apellidoPaterno;
        private String apellidoMaterno;
        private String nombres;
        private String nombreCompleto;
        private LocalDate fechaNacimiento;
        private Integer edad;
        private Byte estado;
        private Long version;
    }
}