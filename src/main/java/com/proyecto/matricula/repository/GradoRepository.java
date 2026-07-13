package com.proyecto.matricula.repository;

import com.proyecto.matricula.entity.Grado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradoRepository extends JpaRepository<Grado, Integer> {
}
