package com.proyecto.matricula.entity;

import com.proyecto.matricula.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "nivel")
public class Nivel extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_nivel")
    private Integer codNivel;

    @Column(name = "nombre_nivel", length = 50, nullable = false, unique = true)
    private String nombreNivel;
}
