package pe.edu.unjfsc.sistemamatricula.entity.academico;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;

/** A, B, C, D... */
@Entity
@Table(name = "seccion")
@Getter @Setter
public class Seccion extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 5)
    private String nombre;

    @Column(nullable = false)
    private Byte estado = 1;

    @Version
    private Long version = 0L;
}