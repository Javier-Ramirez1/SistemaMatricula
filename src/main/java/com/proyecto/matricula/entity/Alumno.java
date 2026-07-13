package com.proyecto.matricula.entity;

import com.proyecto.matricula.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "alumno")
public class Alumno extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_alumno")
    private Integer codAlumno;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_persona", nullable = false, unique = true)
    private Persona persona;

    @Column(name = "nombre_apoderado", length = 120)
    private String nombreApoderado;

    @Column(name = "celular_apoderado", length = 15)
    private String celularApoderado;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
