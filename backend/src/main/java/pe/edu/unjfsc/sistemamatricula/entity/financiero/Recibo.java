package pe.edu.unjfsc.sistemamatricula.entity.financiero;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Recibo: comprobante derivado de un Pago, con número de boleta
 * correlativo (ej: BOL-2026-000001) generado dentro de la misma
 * transacción del SP sp_pagar_cuota, usando SELECT ... FOR UPDATE
 * sobre Correlativo para evitar números repetidos en concurrencia.
 */
@Entity
@Table(name = "recibo")
@Getter @Setter
public class Recibo extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_boleta", nullable = false, unique = true, length = 20)
    private String numeroBoleta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pago", nullable = false)
    private Pago pago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_matricula", nullable = false)
    private Matricula matricula;

    @Column(name = "monto_pagado", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPagado;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false)
    private Byte estado = 1;

    @Version
    private Long version = 0L;

    @PrePersist
    protected void onPrePersist() {
        if (this.fechaEmision == null) this.fechaEmision = LocalDateTime.now();
    }
}