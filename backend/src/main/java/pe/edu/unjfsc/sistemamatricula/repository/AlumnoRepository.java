package pe.edu.unjfsc.sistemamatricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Alumno;

import java.util.List;
import java.util.Optional;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    Optional<Alumno> findByTipoDocumentoIdAndNumeroDocumento(Long idTipoDocumento, String numeroDocumento);

    boolean existsByTipoDocumentoIdAndNumeroDocumento(Long idTipoDocumento, String numeroDocumento);

    List<Alumno> findByEstado(Byte estado);

    List<Alumno> findByNombresContainingIgnoreCaseOrApellidoPaternoContainingIgnoreCaseOrApellidoMaternoContainingIgnoreCase(
            String nombres, String apellidoPaterno, String apellidoMaterno);
}