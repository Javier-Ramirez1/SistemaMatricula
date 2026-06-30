package pe.edu.unjfsc.sistemamatricula.entity.auditoria;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Registro de auditoría: quién hizo qué, cuándo, y con qué datos
 * antes/después. Se llena automáticamente vía AuditoriaListener
 * (entidad propia, NO extiende Auditable — sería circular).
 *
 * datosAntes / datosDespues como String JSON: se serializa con
 * Jackson en AuditoriaService antes de guardar (columna JSON en
 * MySQL acepta texto directamente).
 */
@Entity
@Table(name = "auditoria")
@Getter @Setter
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String tabla;

    @Column(name = "id_registro", nullable = false)
    private Long idRegistro;

    @Column(nullable = false, length = 20)
    private String accion;            // INSERT, UPDATE, DELETE

    @Lob
    @Column(name = "datos_antes", columnDefinition = "JSON")
    private String datosAntes;

    @Lob
    @Column(name = "datos_despues", columnDefinition = "JSON")
    private String datosDespues;

    @Column(nullable = false, length = 50)
    private String usuario;

    @Column(name = "ip_cliente", length = 45)
    private String ipCliente;

    @Column(length = 200)
    private String navegador;

    @Column(name = "version_entity")
    private Long versionEntity;

    @Column(length = 500)
    private String observacion;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        if (this.fecha == null) this.fecha = LocalDateTime.now();
    }
}