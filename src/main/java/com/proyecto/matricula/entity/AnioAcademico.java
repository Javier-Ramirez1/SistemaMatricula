package com.proyecto.matricula.entity;

import com.proyecto.matricula.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "anio_academico")
public class AnioAcademico extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_anio_academico")
    private Integer codAnioAcademico;

    @Column(name = "anio", nullable = false, unique = true)
    private Integer anio;

    @Column(name = "descripcion", length = 50)
    private String descripcion;
}
