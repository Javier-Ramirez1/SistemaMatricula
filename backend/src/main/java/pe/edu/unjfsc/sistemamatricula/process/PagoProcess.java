package pe.edu.unjfsc.sistemamatricula.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.PagarCuotaRequest;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.ReciboResponse;
import pe.edu.unjfsc.sistemamatricula.entity.enums.EstadoPago;
import pe.edu.unjfsc.sistemamatricula.entity.enums.EstadoTransaccionPago;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Correlativo;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Cuota;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Pago;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Recibo;
import pe.edu.unjfsc.sistemamatricula.exception.BusinessException;
import pe.edu.unjfsc.sistemamatricula.exception.ResourceNotFoundException;
import pe.edu.unjfsc.sistemamatricula.mapper.PagoMapper;
import pe.edu.unjfsc.sistemamatricula.repository.CorrelativoRepository;
import pe.edu.unjfsc.sistemamatricula.repository.CuotaRepository;
import pe.edu.unjfsc.sistemamatricula.repository.PagoRepository;
import pe.edu.unjfsc.sistemamatricula.repository.ReciboRepository;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;
import pe.edu.unjfsc.sistemamatricula.util.NumeroUtil;

import java.util.List;

/**
 * Proceso de PAGAR una cuota — actualiza cuota + inserta pago +
 * inserta recibo + incrementa correlativo, todo en una sola
 * transacción ACID (equivalente al sp_pagar_cuota descrito en la
 * sustentación técnica).
 *
 * Pasos:
 *  1) la cuota debe existir y estar PENDIENTE
 *  2) si el frontend envía versionCuota, se compara contra la
 *     versión actual ANTES de tocar nada, para dar un mensaje de
 *     conflicto más claro que el OptimisticLockException genérico
 *  3) regla de orden de cobro: no se puede pagar la cuota N si hay
 *     cuotas de orden menor que sigan PENDIENTES
 *  4) se marca la cuota como PAGADA y se registra el Pago
 *  5) se bloquea el Correlativo del año con SELECT ... FOR UPDATE
 *     y se incrementa para generar un número de boleta único, aun
 *     bajo pagos concurrentes
 *  6) se emite el Recibo
 */
@Component
@RequiredArgsConstructor
public class PagoProcess {

    private final CuotaRepository cuotaRepository;
    private final PagoRepository pagoRepository;
    private final ReciboRepository reciboRepository;
    private final CorrelativoRepository correlativoRepository;
    private final PagoMapper pagoMapper;

    @Transactional
    public ReciboResponse pagarCuota(PagarCuotaRequest request) {

        Cuota cuota = cuotaRepository.findById(request.getIdCuota())
                .orElseThrow(() -> new ResourceNotFoundException("Cuota", request.getIdCuota()));

        if (request.getVersionCuota() != null && !request.getVersionCuota().equals(cuota.getVersion())) {
            throw new BusinessException(
                    "La cuota fue modificada por otro usuario mientras la pantalla estaba abierta. " +
                            "Por favor recarga la lista de deudas e intenta nuevamente.");
        }

        if (cuota.getEstadoPago() != EstadoPago.PENDIENTE) {
            throw new BusinessException("Esta cuota ya fue pagada anteriormente.");
        }

        List<Cuota> cuotasAnterioresPendientes = cuotaRepository.findByMatriculaIdAndOrdenCobroLessThanAndEstadoPago(
                cuota.getMatricula().getId(), cuota.getOrdenCobro(), EstadoPago.PENDIENTE);

        if (!cuotasAnterioresPendientes.isEmpty()) {
            throw new BusinessException(
                    "No se puede pagar esta cuota: existen cuotas anteriores pendientes que deben pagarse primero.");
        }

        cuota.setEstadoPago(EstadoPago.PAGADO);
        cuota = cuotaRepository.save(cuota);

        Pago pago = new Pago();
        pago.setCuota(cuota);
        pago.setMatricula(cuota.getMatricula());
        pago.setMontoPagado(cuota.getMonto());
        pago.setTipoPago(request.getTipoPago());
        pago.setReferenciaPago(request.getReferenciaPago());
        pago.setEstado(EstadoTransaccionPago.PROCESADO);
        pago.setEstadoRegistro(Constantes.ACTIVO);
        pago = pagoRepository.save(pago);

        Long idAnioAcademico = cuota.getMatricula().getAnioAcademico().getId();
        Correlativo correlativo = correlativoRepository.findByAnioAcademicoIdForUpdate(idAnioAcademico)
                .orElseThrow(() -> new BusinessException(
                        "No existe un correlativo de boletas configurado para este año académico."));

        correlativo.setUltimoNumero(correlativo.getUltimoNumero() + 1);
        correlativo = correlativoRepository.save(correlativo);

        String numeroBoleta = NumeroUtil.formatearCorrelativo(
                correlativo.getPrefijo(),
                cuota.getMatricula().getAnioAcademico().getAnio(),
                correlativo.getUltimoNumero());

        Recibo recibo = new Recibo();
        recibo.setNumeroBoleta(numeroBoleta);
        recibo.setPago(pago);
        recibo.setMatricula(cuota.getMatricula());
        recibo.setMontoPagado(pago.getMontoPagado());
        recibo.setEstado(Constantes.ACTIVO);
        recibo = reciboRepository.save(recibo);

        return pagoMapper.toReciboResponse(recibo);
    }
}