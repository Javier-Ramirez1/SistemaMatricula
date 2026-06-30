package pe.edu.unjfsc.sistemamatricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.unjfsc.sistemamatricula.entity.enums.EstadoPago;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Cuota;

import java.util.List;

public interface CuotaRepository extends JpaRepository<Cuota, Long> {

    List<Cuota> findByMatriculaIdOrderByOrdenCobro(Long idMatricula);

    /**
     * Regla de orden de cobro: cuotas con orden MENOR al de la cuota
     * que se quiere pagar, que sigan pendientes — si esta lista NO
     * está vacía, se bloquea el pago (ver PagoProcess).
     */
    List<Cuota> findByMatriculaIdAndOrdenCobroLessThanAndEstadoPago(
            Long idMatricula, Integer ordenCobro, EstadoPago estadoPago);

    List<Cuota> findByMatriculaIdAndEstadoPago(Long idMatricula, EstadoPago estadoPago);
}