package pe.edu.unjfsc.sistemamatricula.entity.financiero;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;
import pe.edu.unjfsc.sistemamatricula.entity.academico.AnioAcademico;

import java.math.BigDecimal;

/**
 * Concepto del tarifario, por año académico.
 *
 * @Version: el campo MÁS importante para el caso de Optimistic Lock
 * que pide el profesor. Si la secretaria tiene la pantalla de
 * matrícula abierta con el precio viejo y alguien sube el monto,
 * JPA detecta el conflicto al hacer save() y lanza
 * OptimisticLockException (capturado por GlobalExceptionHandler).
 *
 * ordenCobro: define la secuencia obligatoria de pago — no se
 * puede pagar la cuota de orden 3 sin haber pagado la de orden 1.
 */
@Entity
@Table(name = "concepto")
@Getter @Setter
public class Concepto extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_anio_academico", nullable = false)
    private AnioAcademico anioAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_concepto", nullable = false)
    private TipoConcepto tipoConcepto;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "orden_cobro", nullable = false)
    private Integer ordenCobro = 1;

    /** Obligatorio para todos los años académicos (no se omite al clonar) */
    @Column(name = "es_default", nullable = false)
    private Byte esDefault = 0;

    @Column(nullable = false)
    private Byte estado = 1;

    /**
     * Optimistic Lock — JPA agrega automáticamente
     * "AND version = ?" al UPDATE y lanza excepción si no coincide.
     * NUNCA se modifica manualmente.
     */
    @Version
    private Long version = 0L;
}