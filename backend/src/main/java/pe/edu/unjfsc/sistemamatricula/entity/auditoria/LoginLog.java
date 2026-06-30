package pe.edu.unjfsc.sistemamatricula.entity.auditoria;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import pe.edu.unjfsc.sistemamatricula.entity.seguridad.Usuario;
/**
 * Bitácora de inicios de sesión — trazabilidad de seguridad.
 * No extiende Auditable porque es en sí misma un registro de
 * auditoría (solo necesita fecha_insert, no los 4 campos completos).
 */
@Entity
@Table(name = "login_log")
@Getter @Setter
public class LoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_login", nullable = false)
    private LocalDateTime fechaLogin;

    @Column(name = "fecha_logout")
    private LocalDateTime fechaLogout;

    @Column(name = "ip_cliente", length = 45)
    private String ipCliente;

    @Column(length = 200)
    private String navegador;

    @Column(nullable = false, length = 20)
    private String resultado;       // EXITO, FALLO

    @Column(name = "motivo_fallo", length = 200)
    private String motivoFallo;

    @Column(name = "sesion_id", length = 100)
    private String sesionId;

    @Column(name = "usuario_insert", length = 50)
    private String usuarioInsert;

    @Column(name = "fecha_insert")
    private LocalDateTime fechaInsert;

    @PrePersist
    protected void onCreate() {
        this.fechaInsert = LocalDateTime.now();
        if (this.fechaLogin == null) this.fechaLogin = LocalDateTime.now();
    }
}