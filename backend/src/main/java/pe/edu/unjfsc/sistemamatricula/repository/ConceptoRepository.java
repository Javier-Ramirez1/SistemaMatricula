package pe.edu.unjfsc.sistemamatricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Concepto;

import java.util.List;

public interface ConceptoRepository extends JpaRepository<Concepto, Long> {

    List<Concepto> findByAnioAcademicoIdAndEstadoOrderByOrdenCobro(Long idAnioAcademico, Byte estado);

    List<Concepto> findByAnioAcademicoIdAndEsDefaultAndEstado(Long idAnioAcademico, Byte esDefault, Byte estado);
}