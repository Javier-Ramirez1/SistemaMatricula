package pe.edu.unjfsc.sistemamatricula.entity.academico;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;

/**
 * Aula: combinación única de año + nivel + grado + sección.
 *
 * cantidadAlumnos se actualiza dentro del SP sp_matricular —
 * por eso @Version es CRÍTICO aquí: si dos secretarias matriculan
 * al mismo tiempo en la misma aula casi llena, el optimistic lock
 * evita que ambas pasen el chequeo de cupo con datos obsoletos.
 */
@Entity
@Table(name = "aula",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_aula",
                columnNames = {"id_anio_academico", "id_nivel", "id_grado", "id_seccion"}
        ))
@Getter @Setter
public class Aula extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_anio_academico", nullable = false)
    private AnioAcademico anioAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nivel", nullable = false)
    private Nivel nivel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_grado", nullable = false)
    private Grado grado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_seccion", nullable = false)
    private Seccion seccion;

    @Column(name = "cantidad_alumnos", nullable = false)
    private Integer cantidadAlumnos = 0;

    @Column(nullable = false)
    private Byte estado = 1;

    /** Optimistic Lock — concurrencia al matricular */
    @Version
    private Long version = 0L;
}