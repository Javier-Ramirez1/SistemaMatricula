package com.proyecto.matricula.service;

import com.proyecto.matricula.entity.Correlativo;
import com.proyecto.matricula.entity.Cuota;
import com.proyecto.matricula.entity.Pago;
import com.proyecto.matricula.repository.CorrelativoRepository;
import com.proyecto.matricula.repository.CuotaRepository;
import com.proyecto.matricula.repository.PagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PagoService {

    private final CuotaRepository cuotaRepository;
    private final PagoRepository pagoRepository;
    private final CorrelativoRepository correlativoRepository;
    private final AuditoriaService auditoriaService;

    public PagoService(CuotaRepository cuotaRepository, PagoRepository pagoRepository,
                       CorrelativoRepository correlativoRepository, AuditoriaService auditoriaService) {
        this.cuotaRepository = cuotaRepository;
        this.pagoRepository = pagoRepository;
        this.correlativoRepository = correlativoRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public Pago registrarPago(Integer codCuota, BigDecimal montoAPagar) {
        // 1. Obtener la cuota activa
        Cuota cuota = cuotaRepository.findByCodCuotaAndEstadoTrue(codCuota)
                .orElseThrow(() -> new IllegalArgumentException("La cuota no existe o está inactiva."));

        if (cuota.getPagado()) {
            throw new IllegalStateException("La cuota seleccionada ya se encuentra totalmente cancelada.");
        }

        if (montoAPagar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a pagar debe ser mayor a cero.");
        }

        if (montoAPagar.compareTo(cuota.getMontoPendiente()) > 0) {
            throw new IllegalArgumentException("El monto de pago excede el saldo pendiente actual de la cuota.");
        }

        // 2. Regla de Negocio: Validar que no existan cuotas anteriores (menor orden de pago) pendientes
        List<Cuota> cuotasPendientes = cuotaRepository.findPendingCuotas(
                cuota.getMatricula().getAlumno().getCodAlumno(),
                cuota.getMatricula().getAnioAcademico().getCodAnioAcademico()
        );

        if (!cuotasPendientes.isEmpty()) {
            Cuota cuotaPrimeraPendiente = cuotasPendientes.get(0);
            if (cuotaPrimeraPendiente.getOrdenPago() < cuota.getOrdenPago()) {
                throw new IllegalStateException("No se puede pagar esta cuota. Primero debe cancelar la cuota anterior pendiente: " 
                        + cuotaPrimeraPendiente.getConcepto().getNombreConcepto());
            }
        }

        // 3. Generar número correlativo de recibo seguro (Pessimistic Lock)
        Correlativo correlativo = correlativoRepository.findAndLockByTipoAndSerie("RECIBO", "R001")
                .orElseThrow(() -> new IllegalStateException("No se encontró el correlativo de RECIBO R001 configurado en el sistema."));

        int nuevoNumero = correlativo.getUltimoNumero() + 1;
        correlativo.setUltimoNumero(nuevoNumero);
        correlativoRepository.save(correlativo);

        // 4. Registrar Pago
        Pago pago = new Pago();
        pago.setCuota(cuota);
        pago.setMontoPagado(montoAPagar);
        pago.setTipoComprobante(correlativo.getTipoComprobante());
        pago.setSerieComprobante(correlativo.getSerie());
        pago.setNumeroComprobante(nuevoNumero);
        pago.setEstado(true);
        pago = pagoRepository.save(pago);

        // 5. Actualizar saldo de la cuota y marcar como pagada
        BigDecimal nuevoSaldo = cuota.getMontoPendiente().subtract(montoAPagar);
        cuota.setMontoPendiente(nuevoSaldo);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) == 0) {
            cuota.setPagado(true);
        }
        cuotaRepository.save(cuota);

        // 6. Registrar Auditoría
        auditoriaService.registrar(
                "CUENTAS Y PAGOS",
                "pago",
                "INSERT",
                pago.getCodPago(),
                null,
                "Pago registrado por un monto de " + montoAPagar + " para la cuota ID " + codCuota + " con comprobante " + correlativo.getSerie() + "-" + nuevoNumero
        );

        return pago;
    }
}
