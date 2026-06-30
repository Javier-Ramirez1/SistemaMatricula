package pe.edu.unjfsc.sistemamatricula.entity.financiero;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;

/** OBLIGATORIO, OPCIONAL */
@Entity
@Table(name = "tipo_concepto")
@Getter @Setter
public class TipoConcepto extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(nullable = false)
    private Byte estado = 1;

    @Version
    private Long version = 0L;
}