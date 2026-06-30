package pe.edu.unjfsc.sistemamatricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Nivel;

import java.util.List;
import java.util.Optional;

public interface NivelRepository extends JpaRepository<Nivel, Long> {
    Optional<Nivel> findByNombre(String nombre);
    List<Nivel> findByEstado(Byte estado);
}