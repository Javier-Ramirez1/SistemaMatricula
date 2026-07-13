package com.proyecto.matricula.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tipo_concepto")
public class TipoConcepto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_tipo_concepto")
    private Integer codTipoConcepto;

    @Column(name = "descripcion", length = 50, nullable = false, unique = true)
    private String descripcion;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
