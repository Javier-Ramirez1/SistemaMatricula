package pe.edu.unjfsc.sistemamatricula.entity.academico;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;

/**
 * Catálogo de tipos de documento (DNI, CE, PAS).
 * Usado tanto por Usuario como por Alumno.
 */
@Entity
@Table(name = "tipo_documento")
@Getter @Setter
public class TipoDocumento extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String codigo;       // DNI, CE, PAS

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(nullable = false)
    private Byte estado = 1;
}