package com.proyecto.matricula.repository;

import com.proyecto.matricula.entity.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Integer> {
    Optional<Matricula> findByCodMatriculaAndEstadoTrue(Integer codMatricula);
    java.util.List<Matricula> findAllByEstadoTrue();
    java.util.List<Matricula> findAllByAulaNivelCodNivelAndEstadoTrue(Integer codNivel);
    java.util.List<Matricula> findAllByAnioAcademicoCodAnioAcademicoAndEstadoTrue(Integer codAnioAcademico);
    java.util.List<Matricula> findAllByAnioAcademicoCodAnioAcademicoAndAulaNivelCodNivelAndEstadoTrue(Integer codAnioAcademico, Integer codNivel);

    // Validar si el alumno ya está matriculado en el año académico
    boolean existsByAnioAcademicoCodAnioAcademicoAndAlumnoCodAlumnoAndEstadoTrue(
            Integer codAnioAcademico, Integer codAlumno);

    long countByAulaNivelNombreNivelAndEstadoTrue(String nombreNivel);
    long countByAnioAcademicoAnioAndAulaNivelNombreNivelAndEstadoTrue(Integer anio, String nombreNivel);
    long countByAnioAcademicoAnioAndEstadoTrue(Integer anio);
}
