package pe.edu.unjfsc.sistemamatricula.entity.financiero;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;
import pe.edu.unjfsc.sistemamatricula.entity.academico.AnioAcademico;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Alumno;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Aula;
import pe.edu.unjfsc.sistemamatricula.entity.enums.EstadoMatricula;

import java.time.LocalDate;

/**
 * Matrícula: un alumno en un aula, en un año académico.
 *
 * UNIQUE(id_alumno, id_anio_academico): garantiza a nivel de BD
 * que un alumno no puede tener dos matrículas en el mismo año
 * (regla de negocio + restricción física, doble seguridad).
 */
@Entity
@Table(name = "matricula",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_matricula_alumno_anio",
                columnNames = {"id_alumno", "id_anio_academico"}
        ))
@Getter @Setter
public class Matricula extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_anio_academico", nullable = false)
    private AnioAcademico anioAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alumno", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aula", nullable = false)
    private Aula aula;

    @Column(name = "fecha_matricula", nullable = false)
    private LocalDate fechaMatricula;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_matricula", nullable = false, length = 20)
    private EstadoMatricula estadoMatricula = EstadoMatricula.ACTIVA;

    @Column(nullable = false)
    private Byte estado = 1;

    @Version
    private Long version = 0L;
}