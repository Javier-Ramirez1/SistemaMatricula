package com.proyecto.matricula.entity;

import com.proyecto.matricula.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "concepto", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cod_anio_academico", "nombre_concepto"})
})
public class Concepto extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_concepto")
    private Integer codConcepto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_anio_academico", nullable = false)
    private AnioAcademico anioAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_tipo_concepto", nullable = false)
    private TipoConcepto tipoConcepto;

    @Column(name = "nombre_concepto", length = 80, nullable = false)
    private String nombreConcepto;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "orden_pago", nullable = false)
    private Short ordenPago;

    @Column(name = "obligatorio", nullable = false)
    private Boolean obligatorio = true;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;
}
