package pe.edu.unjfsc.sistemamatricula.service.financiero;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.ReciboResponse;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Recibo;
import pe.edu.unjfsc.sistemamatricula.exception.ResourceNotFoundException;
import pe.edu.unjfsc.sistemamatricula.mapper.PagoMapper;
import pe.edu.unjfsc.sistemamatricula.repository.ReciboRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Consulta de recibos ya emitidos (la emisión ocurre dentro de PagoProcess). */
@Service
@RequiredArgsConstructor
public class ReciboService {

    private final ReciboRepository reciboRepository;
    private final PagoMapper pagoMapper;

    @Transactional(readOnly = true)
    public List<ReciboResponse> listarPorMatricula(Long idMatricula) {
        return reciboRepository.findByMatriculaIdOrderByFechaEmisionDesc(idMatricula).stream()
                .map(pagoMapper::toReciboResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReciboResponse obtenerPorNumeroBoleta(String numeroBoleta) {
        Recibo recibo = reciboRepository.findByNumeroBoleta(numeroBoleta)
                .orElseThrow(() -> new ResourceNotFoundException("Recibo con boleta " + numeroBoleta + " no encontrado"));
        return pagoMapper.toReciboResponse(recibo);
    }
}