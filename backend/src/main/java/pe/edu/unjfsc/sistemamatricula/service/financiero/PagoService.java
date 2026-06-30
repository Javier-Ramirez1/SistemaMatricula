package pe.edu.unjfsc.sistemamatricula.service.financiero;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.entity.enums.EstadoPago;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Cuota;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Pago;
import pe.edu.unjfsc.sistemamatricula.exception.ResourceNotFoundException;
import pe.edu.unjfsc.sistemamatricula.repository.CuotaRepository;
import pe.edu.unjfsc.sistemamatricula.repository.PagoRepository;

import java.util.List;

/**
 * Capa de CONSULTA de pagos (lista de deudas, histórico). El proceso
 * de PAGAR (que actualiza cuota + inserta pago + inserta recibo +
 * incrementa correlativo, todo en una sola transacción ACID) vive
 * en PagoProcess — ver el pseudocódigo de la sustentación técnica.
 */
@Service
@RequiredArgsConstructor
public class PagoService {

    private final CuotaRepository cuotaRepository;
    private final PagoRepository pagoRepository;

    @Transactional(readOnly = true)
    public List<Cuota> listarDeudasPorMatricula(Long idMatricula) {
        return cuotaRepository.findByMatriculaIdOrderByOrdenCobro(idMatricula);
    }

    @Transactional(readOnly = true)
    public List<Cuota> listarDeudasPendientes(Long idMatricula) {
        return cuotaRepository.findByMatriculaIdAndEstadoPago(idMatricula, EstadoPago.PENDIENTE);
    }

    @Transactional(readOnly = true)
    public List<Pago> listarHistorialPorMatricula(Long idMatricula) {
        return pagoRepository.findByMatriculaIdOrderByFechaPagoDesc(idMatricula);
    }

    @Transactional(readOnly = true)
    public Cuota obtenerCuotaPorId(Long id) {
        return cuotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuota", id));
    }
}