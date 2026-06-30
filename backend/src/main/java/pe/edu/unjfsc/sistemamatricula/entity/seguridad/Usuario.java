package pe.edu.unjfsc.sistemamatricula.entity.seguridad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;
import pe.edu.unjfsc.sistemamatricula.entity.academico.TipoDocumento;
import java.util.HashSet;
import java.util.Set;

/**
 * Usuario del sistema (superusuario, director, secretaria).
 *
 * esSuperusuario = true → protegido contra eliminación,
 * validado en UsuarioService antes del borrado lógico, no aquí
 * (la entity no debe contener reglas de negocio).
 *
 * UNIQUE(id_tipo_documento, numero_documento): el mismo uniqueKey
 * por tipo+número de documento que usa Alumno, aplicado también
 * al personal del sistema.
 */
@Entity
@Table(name = "usuario",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_usuario_doc",
                columnNames = {"id_tipo_documento", "numero_documento"}
        ))
@Getter @Setter
public class Usuario extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(length = 100)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false, length = 20)
    private String numeroDocumento;

    @Column(name = "es_superusuario", nullable = false)
    private Byte esSuperusuario = 0;

    @Column(nullable = false)
    private Byte estado = 1;

    @Version
    private Long version = 0L;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_rol",
            joinColumns        = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Rol> roles = new HashSet<>();

    @Transient
    public boolean isSuperusuario() {
        return esSuperusuario != null && esSuperusuario == 1;
    }
}