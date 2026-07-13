package com.proyecto.matricula.entity;


import com.proyecto.matricula.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "matricula", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cod_anio_academico", "cod_alumno"})
})
public class Matricula extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_matricula")
    private Integer codMatricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_anio_academico", nullable = false)
    private AnioAcademico anioAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_aula", nullable = false)
    private Aula aula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_alumno", nullable = false)
    private Alumno alumno;

    @Column(name = "fecha_matricula", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaMatricula;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;
}

