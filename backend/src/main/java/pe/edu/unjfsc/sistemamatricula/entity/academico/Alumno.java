package pe.edu.unjfsc.sistemamatricula.entity.academico;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pe.edu.unjfsc.sistemamatricula.entity.Auditable;

import java.time.LocalDate;

/**
 * Alumno = "cliente" del sistema (nunca acepta RUC, solo
 * documentos de persona natural: DNI, CE, Pasaporte).
 *
 * UNIQUE(id_tipo_documento, numero_documento): uniqueKey que
 * impide duplicar al mismo alumno con el mismo documento.
 */
@Entity
@Table(name = "alumno",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_alumno_doc",
                columnNames = {"id_tipo_documento", "numero_documento"}
        ))
@Getter @Setter
public class Alumno extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false, length = 20)
    private String numeroDocumento;

    @Column(name = "apellido_paterno", nullable = false, length = 80)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", nullable = false, length = 80)
    private String apellidoMaterno;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private Byte estado = 1;

    @Version
    private Long version = 0L;

    @Transient
    public String getNombreCompleto() {
        return apellidoPaterno + " " + apellidoMaterno + ", " + nombres;
    }
}