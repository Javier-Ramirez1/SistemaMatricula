package com.proyecto.matricula.repository;

import com.proyecto.matricula.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {
    List<Auditoria> findAllByOrderByFechaHoraDesc();
    List<Auditoria> findTop50ByOrderByFechaHoraDesc();
}
