package pe.edu.unjfsc.sistemamatricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pe.edu.unjfsc.sistemamatricula.entity.auditoria.Auditoria;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long>, JpaSpecificationExecutor<Auditoria> {
    List<Auditoria> findByTablaAndIdRegistroOrderByFechaDesc(String tabla, Long idRegistro);
    List<Auditoria> findTop100ByOrderByFechaDesc();
}