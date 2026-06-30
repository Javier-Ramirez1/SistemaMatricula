package pe.edu.unjfsc.sistemamatricula.entity.academico;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;

/** PRIMERO, SEGUNDO... dentro de un Nivel, con un orden de visualización */
@Entity
@Table(name = "grado")
@Getter @Setter
public class Grado extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nivel", nullable = false)
    private Nivel nivel;

    @Column(nullable = false)
    private Integer orden = 1;

    @Column(nullable = false)
    private Byte estado = 1;

    @Version
    private Long version = 0L;
}