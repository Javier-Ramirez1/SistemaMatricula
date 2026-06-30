package pe.edu.unjfsc.sistemamatricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.unjfsc.sistemamatricula.entity.academico.TipoDocumento;

import java.util.List;
import java.util.Optional;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {
    Optional<TipoDocumento> findByCodigo(String codigo);
    List<TipoDocumento> findByEstado(Byte estado);
}