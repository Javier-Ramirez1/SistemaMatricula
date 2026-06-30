package pe.edu.unjfsc.sistemamatricula.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Clase base con campos de auditoría comunes a TODAS las entidades.
 *
 * @MappedSuperclass: no genera tabla propia, sus campos se mapean
 * directamente en las tablas hijas (usuario, alumno, concepto, etc).
 *
 * @EntityListeners(AuditingEntityListener.class): conecta esta clase
 * con el AuditorAware configurado en JpaConfig. @CreatedBy y
 * @LastModifiedBy se rellenan SOLOS con el username del JWT,
 * sin tener que pasarlo manualmente en cada Service.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public abstract class Auditable {

    @CreatedBy
    @Column(name = "usuario_insert", length = 50, updatable = false)
    private String usuarioInsert;

    @CreatedDate
    @Column(name = "fecha_insert", updatable = false)
    private LocalDateTime fechaInsert;

    @LastModifiedBy
    @Column(name = "usuario_update", length = 50)
    private String usuarioUpdate;

    @LastModifiedDate
    @Column(name = "fecha_update")
    private LocalDateTime fechaUpdate;
}