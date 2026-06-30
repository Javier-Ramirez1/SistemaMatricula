package pe.edu.unjfsc.sistemamatricula.entity.financiero;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;
import pe.edu.unjfsc.sistemamatricula.entity.enums.EstadoTransaccionPago;
import pe.edu.unjfsc.sistemamatricula.entity.enums.TipoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pago: el evento financiero en sí (efectivo, Yape, transferencia...).
 * Separado de Recibo porque el pago es la transacción, y el recibo
 * es el comprobante generado A PARTIR de un pago ya procesado.
 *
 * Este diseño permite, por ejemplo, registrar un pago RECHAZADO
 * (tarjeta declinada) sin que eso genere un recibo válido.
 */
@Entity
@Table(name = "pago")
@Getter @Setter
public class Pago extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuota", nullable = false)
    private Cuota cuota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_matricula", nullable = false)
    private Matricula matricula;

    @Column(name = "monto_pagado", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPagado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false, length = 20)
    private TipoPago tipoPago;

    @Column(name = "referencia_pago", length = 100)
    private String referenciaPago;   // código de operación Yape, voucher, etc.

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTransaccionPago estado = EstadoTransaccionPago.PROCESADO;

    @Column(name = "estado_registro", nullable = false)
    private Byte estadoRegistro = 1;

    @Version
    private Long version = 0L;

    @PrePersist
    protected void onPrePersist() {
        if (this.fechaPago == null) this.fechaPago = LocalDateTime.now();
    }
}