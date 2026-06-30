package pe.edu.unjfsc.sistemamatricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Grado;

import java.util.List;

public interface GradoRepository extends JpaRepository<Grado, Long> {
    List<Grado> findByNivelIdAndEstadoOrderByOrden(Long idNivel, Byte estado);
    List<Grado> findByEstado(Byte estado);
}