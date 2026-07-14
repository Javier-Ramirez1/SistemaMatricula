package com.proyecto.matricula.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CorrelativoId implements Serializable {
    private String tipoComprobante;
    private String serie;
}
