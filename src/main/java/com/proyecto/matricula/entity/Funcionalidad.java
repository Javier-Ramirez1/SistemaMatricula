package com.proyecto.matricula.entity;

import com.proyecto.matricula.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "funcionalidad")
public class Funcionalidad extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_funcionalidad")
    private Integer idFuncionalidad;

    @Column(name = "nombre", length = 80, nullable = false, unique = true)
    private String nombre;

    @Column(name = "icono", length = 60)
    private String icono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "padre")
    private Funcionalidad padre;
}
