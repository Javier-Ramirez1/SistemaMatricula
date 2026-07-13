package com.proyecto.matricula.entity;

import com.proyecto.matricula.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "persona")
public class Persona extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Integer idPersona;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cod_tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", length = 15, nullable = false)
    private String numeroDocumento;

    @Column(name = "nombres", length = 80, nullable = false)
    private String nombres;

    @Column(name = "apellido_paterno", length = 60, nullable = false)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 60, nullable = false)
    private String apellidoMaterno;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "celular", length = 15)
    private String celular;

    @Column(name = "correo_electronico", length = 100)
    private String correoElectronico;

    @Column(name = "direccion", length = 150)
    private String direccion;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
