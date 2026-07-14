package com.proyecto.matricula.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "correlativo")
@IdClass(CorrelativoId.class)
public class Correlativo {

    @Id
    @Column(name = "tipo_comprobante", length = 20)
    private String tipoComprobante;

    @Id
    @Column(name = "serie", length = 4)
    private String serie;

    @Column(name = "ultimo_numero", nullable = false)
    private Integer ultimoNumero = 0;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
