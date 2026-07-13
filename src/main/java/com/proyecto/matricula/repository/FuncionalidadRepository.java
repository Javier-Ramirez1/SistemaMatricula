package com.proyecto.matricula.repository;

import com.proyecto.matricula.entity.Funcionalidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuncionalidadRepository extends JpaRepository<Funcionalidad, Integer> {
    List<Funcionalidad> findAllByEstadoTrue();
}
