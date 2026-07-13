package com.proyecto.matricula.repository;

import com.proyecto.matricula.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer> {
    Optional<Persona> findByTipoDocumentoCodTipoDocumentoAndNumeroDocumentoAndEstadoTrue(Integer codTipoDocumento, String numeroDocumento);
}
