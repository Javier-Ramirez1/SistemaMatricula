package com.proyecto.matricula.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tipo_documento")
public class TipoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_tipo_documento")
    private Integer codTipoDocumento;

    @Column(name = "descripcion", length = 30, nullable = false, unique = true)
    private String descripcion;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
