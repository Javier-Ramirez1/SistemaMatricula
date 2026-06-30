package pe.edu.unjfsc.sistemamatricula.entity.seguridad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;

/**
 * Permiso atómico: modulo + submodulo + accion.
 * Ejemplo: ACADEMICO / MATRICULA / CREAR
 *
 * Esta estructura jerárquica es la que alimenta el árbol de
 * checkboxes (jstree) en el panel del superusuario: cada nodo
 * del árbol = un registro de esta tabla.
 */
@Entity
@Table(name = "permiso",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_permiso",
                columnNames = {"modulo", "submodulo", "accion"}
        ))
@Getter @Setter
public class Permiso extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String modulo;       // ACADEMICO, FINANZAS

    @Column(nullable = false, length = 60)
    private String submodulo;    // ALUMNO, MATRICULA, CONCEPTO, PAGO

    @Column(nullable = false, length = 60)
    private String accion;       // CREAR, LEER, ACTUALIZAR, ELIMINAR, ANULAR, PROCESAR, EMITIR

    @Column(length = 200)
    private String descripcion;

    @Column(nullable = false)
    private Byte estado = 1;

    @Version
    private Long version = 0L;

    /**
     * Código compuesto para usar en @PreAuthorize, ej:
     * "ACADEMICO_MATRICULA_CREAR" → hasAuthority("ACADEMICO_MATRICULA_CREAR")
     */
    @Transient
    public String getCodigoCompleto() {
        return modulo + "_" + submodulo + "_" + accion;
    }
}