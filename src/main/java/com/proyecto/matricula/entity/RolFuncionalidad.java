package com.proyecto.matricula.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rol_funcionalidad", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_rol", "id_funcionalidad"})
})
public class RolFuncionalidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol_funcionalidad")
    private Integer idRolFuncionalidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_funcionalidad", nullable = false)
    private Funcionalidad funcionalidad;

    @Column(name = "ver", nullable = false)
    private Boolean ver = false;

    @Column(name = "crear", nullable = false)
    private Boolean crear = false;

    @Column(name = "editar", nullable = false)
    private Boolean editar = false;

    @Column(name = "eliminar", nullable = false)
    private Boolean eliminar = false;

    @Column(name = "imprimir", nullable = false)
    private Boolean imprimir = false;
}
