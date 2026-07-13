package com.proyecto.matricula.repository;

import com.proyecto.matricula.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {
    List<Alumno> findAllByEstadoTrue();
    Optional<Alumno> findByCodAlumnoAndEstadoTrue(Integer codAlumno);

    Optional<Alumno> findByPersonaTipoDocumentoCodTipoDocumentoAndPersonaNumeroDocumentoAndPersonaEstadoTrue(
            Integer codTipoDocumento, String numeroDocumento);
}
