package com.proyecto.matricula.repository;

import com.proyecto.matricula.entity.RolFuncionalidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolFuncionalidadRepository extends JpaRepository<RolFuncionalidad, Integer> {
    List<RolFuncionalidad> findByRolIdRol(Integer idRol);
}
