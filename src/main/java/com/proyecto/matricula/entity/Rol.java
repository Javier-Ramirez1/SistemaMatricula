package com.proyecto.matricula.entity;

import com.proyecto.matricula.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rol")
public class Rol extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "nombre_rol", length = 40, nullable = false, unique = true)
    private String nombreRol;

    @Column(name = "descripcion", length = 150)
    private String descripcion;
}
