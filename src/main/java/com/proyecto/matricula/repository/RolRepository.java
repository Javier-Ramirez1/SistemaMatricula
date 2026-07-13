package com.proyecto.matricula.repository;

import com.proyecto.matricula.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombreRolAndEstadoTrue(String nombreRol);
    java.util.List<Rol> findByEstadoTrue();
    Optional<Rol> findByIdRolAndEstadoTrue(Integer idRol);
}
