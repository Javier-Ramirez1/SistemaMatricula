package pe.edu.unjfsc.sistemamatricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Pago;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByMatriculaIdOrderByFechaPagoDesc(Long idMatricula);
    List<Pago> findByCuotaId(Long idCuota);
}