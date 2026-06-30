package pe.edu.unjfsc.sistemamatricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.unjfsc.sistemamatricula.entity.academico.AnioAcademico;

import java.util.List;
import java.util.Optional;

public interface AnioAcademicoRepository extends JpaRepository<AnioAcademico, Long> {
    Optional<AnioAcademico> findByAnio(Integer anio);
    Optional<AnioAcademico> findByActivo(Byte activo);
    List<AnioAcademico> findByEstadoOrderByAnioDesc(Byte estado);
}