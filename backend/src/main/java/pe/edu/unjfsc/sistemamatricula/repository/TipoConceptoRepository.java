package pe.edu.unjfsc.sistemamatricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.TipoConcepto;

import java.util.List;

public interface TipoConceptoRepository extends JpaRepository<TipoConcepto, Long> {
    List<TipoConcepto> findByEstado(Byte estado);
}
