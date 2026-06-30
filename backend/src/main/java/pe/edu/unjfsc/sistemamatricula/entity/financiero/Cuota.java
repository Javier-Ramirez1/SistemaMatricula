package pe.edu.unjfsc.sistemamatricula.entity.financiero;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;
import pe.edu.unjfsc.sistemamatricula.entity.enums.EstadoPago;

import java.math.BigDecimal;

/**
 * Cuota: una obligación de pago generada al matricular,
 * derivada de un Concepto vigente en ese año.
 *
 * El monto se COPIA del concepto al momento de generar la cuota
 * (no se referencia en vivo) — así si el concepto cambia de precio
 * después, las cuotas ya generadas conservan el precio original
 * con el que se matriculó el alumno.
 *
 * @Version: necesario porque el estado_pago puede cambiar entre
 * que la secretaria carga la lista de deudas y confirma el pago.
 */
@Entity
@Table(name = "cuota")
@Getter @Setter
public class Cuota extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_matricula", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_concepto", nullable = false)
    private Concepto concepto;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "orden_cobro", nullable = false)
    private Integer ordenCobro;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false, length = 20)
    private EstadoPago estadoPago = EstadoPago.PENDIENTE;

    @Column(nullable = false)
    private Byte estado = 1;

    @Version
    private Long version = 0L;
}