package com.proyecto.matricula.entity;


import com.proyecto.matricula.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "cuota")
public class Cuota extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_cuota")
    private Integer codCuota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_matricula", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_concepto", nullable = false)
    private Concepto concepto;

    @Column(name = "monto_original", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoOriginal;

    @Column(name = "monto_pendiente", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPendiente;

    @Column(name = "orden_pago", nullable = false)
    private Short ordenPago;

    @Column(name = "pagado", nullable = false)
    private Boolean pagado = false;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;
}
