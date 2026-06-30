package pe.edu.unjfsc.sistemamatricula.mapper;

import org.springframework.stereotype.Component;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.ReciboResponse;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Recibo;

@Component
public class PagoMapper {

    public ReciboResponse toReciboResponse(Recibo recibo) {
        if (recibo == null) return null;
        return new ReciboResponse(
                recibo.getId(),
                recibo.getNumeroBoleta(),
                recibo.getMatricula().getAlumno().getNombreCompleto(),
                recibo.getPago().getCuota().getConcepto().getNombre(),
                recibo.getMontoPagado(),
                recibo.getFechaEmision(),
                recibo.getPago().getTipoPago().name(),
                recibo.getUsuarioInsert()
        );
    }
}