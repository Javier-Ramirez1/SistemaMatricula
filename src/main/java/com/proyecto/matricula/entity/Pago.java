package com.proyecto.matricula.entity;

import com.proyecto.matricula.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "pago")
public class Pago extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_pago")
    private Integer codPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_cuota", nullable = false)
    private Cuota cuota;

    @Column(name = "monto_pagado", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPagado;

    @Column(name = "fecha_pago", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaPago;

    @Column(name = "tipo_comprobante", length = 20, nullable = false)
    private String tipoComprobante;

    @Column(name = "serie_comprobante", length = 4, nullable = false)
    private String serieComprobante;

    @Column(name = "numero_comprobante", nullable = false)
    private Integer numeroComprobante;
}
