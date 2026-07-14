package com.proyecto.matricula.repository;

import com.proyecto.matricula.entity.Concepto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConceptoRepository extends JpaRepository<Concepto, Integer> {
    List<Concepto> findByAnioAcademicoCodAnioAcademicoAndEstadoTrueOrderByOrdenPagoAsc(Integer codAnioAcademico);
    Optional<Concepto> findByCodConceptoAndEstadoTrue(Integer codConcepto);

    // Validar duplicado
    boolean existsByAnioAcademicoCodAnioAcademicoAndNombreConceptoAndEstadoTrue(
            Integer codAnioAcademico, String nombreConcepto);
}
