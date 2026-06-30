package pe.edu.unjfsc.sistemamatricula.entity.academico;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;

/** INICIAL, PRIMARIA, SECUNDARIA */
@Entity
@Table(name = "nivel")
@Getter @Setter
public class Nivel extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(nullable = false)
    private Byte estado = 1;

    @Version
    private Long version = 0L;
}