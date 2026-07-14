package com.proyecto.matricula.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_auditoria")
    private Integer codAuditoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_usuario")
    private Usuario usuario;

    @Column(name = "modulo", length = 50, nullable = false)
    private String modulo;

    @Column(name = "tabla_afectada", length = 50, nullable = false)
    private String tablaAfectada;

    @Column(name = "operacion", length = 20, nullable = false)
    private String operacion;

    @Column(name = "codigo_registro")
    private Integer codigoRegistro;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(name = "valor_nuevo", columnDefinition = "TEXT")
    private String valorNuevo;

    @Column(name = "fecha_hora", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaHora;

    @Column(name = "ip_origen", length = 45, nullable = false)
    private String ipOrigen;

    @Column(name = "equipo", length = 100)
    private String equipo;

    @Column(name = "navegador", length = 150)
    private String navegador;
}
