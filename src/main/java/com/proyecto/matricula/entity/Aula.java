package com.proyecto.matricula.entity;

import com.proyecto.matricula.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "aula", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cod_anio_academico", "cod_nivel", "cod_grado", "seccion"})
})
public class Aula extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_aula")
    private Integer codAula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_anio_academico", nullable = false)
    private AnioAcademico anioAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_nivel", nullable = false)
    private Nivel nivel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_grado", nullable = false)
    private Grado grado;

    @Column(name = "seccion", length = 2, nullable = false)
    private String seccion;

    @Column(name = "capacidad_maxima", nullable = false)
    private Short capacidadMaxima = 35;

    @Column(name = "vacantes_disponibles", nullable = false)
    private Short vacantesDisponibles;
}
