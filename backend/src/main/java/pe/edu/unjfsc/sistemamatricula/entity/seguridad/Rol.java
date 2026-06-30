package pe.edu.unjfsc.sistemamatricula.entity.seguridad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rol")
@Getter @Setter
public class Rol extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;       // SUPERUSUARIO, DIRECTOR, SECRETARIA, DOCENTE

    @Column(length = 200)
    private String descripcion;

    @Column(nullable = false)
    private Byte estado = 1;

    @Version
    private Long version = 0L;

    /**
     * Relación ManyToMany con Permiso vía tabla puente rol_permiso.
     * FetchType.EAGER porque UserDetailsServiceImpl necesita los
     * permisos disponibles inmediatamente al construir el JWT.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "rol_permiso",
            joinColumns        = @JoinColumn(name = "id_rol"),
            inverseJoinColumns = @JoinColumn(name = "id_permiso")
    )
    private Set<Permiso> permisos = new HashSet<>();
}