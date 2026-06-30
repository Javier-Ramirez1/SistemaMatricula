package pe.edu.unjfsc.sistemamatricula.entity.academico;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;

/**
 * Año académico — pieza central de la trazabilidad del sistema.
 * Aula, Concepto, Matrícula y Correlativo dependen de este año
 * para mantener históricos separados (2025, 2026, 2027...).
 */
@Entity
@Table(name = "anio_academico")
@Getter @Setter
public class AnioAcademico extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer anio;

    @Column(nullable = false)
    private Byte activo = 1;

    @Column(nullable = false)
    private Byte estado = 1;

    @Version
    private Long version = 0L;
}