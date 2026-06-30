package pe.edu.unjfsc.sistemamatricula.entity.seguridad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;

@Entity
@Table(
        name = "rol_permiso",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_rol", "id_permiso"})
        }
)
@Getter
@Setter
public class RolPermiso extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol_permiso")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_permiso", nullable = false)
    private Permiso permiso;

    @Column(nullable = false)
    private Byte estado = 1;

    @Version
    private Long version;
}