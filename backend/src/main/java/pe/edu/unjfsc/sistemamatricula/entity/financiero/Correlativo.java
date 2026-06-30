package pe.edu.unjfsc.sistemamatricula.entity.financiero;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;
import pe.edu.unjfsc.sistemamatricula.entity.academico.AnioAcademico;

/**
 * Correlativo de boletas, un registro por año académico.
 *
 * CRÍTICO: en el SP de pago se protege con SELECT ... FOR UPDATE
 * (bloqueo de fila a nivel de BD), NO solo con @Version. Esto es
 * intencional: el optimistic lock de JPA sirve si dos usuarios
 * EDITAN el mismo registro desde pantallas distintas, pero para
 * incrementar un contador bajo alta concurrencia se necesita el
 * bloqueo pesimista del FOR UPDATE — evita que dos pagos
 * simultáneos generen el mismo número de boleta.
 */
@Entity
@Table(name = "correlativo")
@Getter @Setter
public class Correlativo extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_anio_academico", nullable = false, unique = true)
    private AnioAcademico anioAcademico;

    @Column(nullable = false, length = 10)
    private String prefijo = "BOL";

    @Column(name = "ultimo_numero", nullable = false)
    private Long ultimoNumero = 0L;

    @Column(nullable = false)
    private Byte estado = 1;

    @Version
    private Long version = 0L;
}