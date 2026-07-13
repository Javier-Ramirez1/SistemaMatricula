package com.proyecto.matricula.repository;

import com.proyecto.matricula.entity.AnioAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnioAcademicoRepository extends JpaRepository<AnioAcademico, Integer> {
    Optional<AnioAcademico> findByAnioAndEstadoTrue(Integer anio);
    java.util.List<AnioAcademico> findAllByEstadoTrue();
}
