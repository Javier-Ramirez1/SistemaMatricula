package com.proyecto.matricula.repository;

import com.proyecto.matricula.entity.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Integer> {
    List<Aula> findAllByEstadoTrue();
    List<Aula> findAllByNivelCodNivelAndEstadoTrue(Integer codNivel);
    Optional<Aula> findByCodAulaAndEstadoTrue(Integer codAula);

    // Verificar duplicado antes de crear/editar
    boolean existsByAnioAcademicoCodAnioAcademicoAndNivelCodNivelAndGradoCodGradoAndSeccionAndEstadoTrue(
            Integer codAnioAcademico, Integer codNivel, Integer codGrado, String seccion);

    long countByEstadoTrue();
}
