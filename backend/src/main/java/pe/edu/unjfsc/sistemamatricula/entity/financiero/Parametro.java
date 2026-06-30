package pe.edu.unjfsc.sistemamatricula.entity.financiero;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;

/**
 * Tabla de parámetros configurables del sistema, ej:
 * MAX_ALUMNOS_POR_AULA = "35"
 *
 * @Version aquí SÍ es crítico: dos administradores podrían cambiar
 * el mismo parámetro a la vez (ej. el límite de alumnos por aula).
 */
@Entity
@Table(name = "parametro")
@Getter @Setter
public class Parametro extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String valor;

    @Column(length = 200)
    private String descripcion;

    @Column(nullable = false)
    private Byte estado = 1;

    @Version
    private Long version = 0L;
}