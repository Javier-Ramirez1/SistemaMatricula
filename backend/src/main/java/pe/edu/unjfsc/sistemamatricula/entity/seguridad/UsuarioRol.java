package pe.edu.unjfsc.sistemamatricula.entity.seguridad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;

@Entity
@Table(
        name = "usuario_rol",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_usuario", "id_rol"})
        }
)
@Getter
@Setter
public class UsuarioRol extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_rol")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private Byte estado = 1;

    @Version
    private Long version;
}