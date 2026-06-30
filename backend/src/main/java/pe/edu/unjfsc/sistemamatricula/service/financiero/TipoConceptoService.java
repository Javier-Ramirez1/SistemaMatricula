package pe.edu.unjfsc.sistemamatricula.service.financiero;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.TipoConcepto;
import pe.edu.unjfsc.sistemamatricula.exception.ResourceNotFoundException;
import pe.edu.unjfsc.sistemamatricula.repository.TipoConceptoRepository;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoConceptoService {

    private final TipoConceptoRepository tipoConceptoRepository;

    @Transactional(readOnly = true)
    public List<TipoConcepto> listarActivos() {
        return tipoConceptoRepository.findByEstado(Constantes.ACTIVO);
    }

    @Transactional(readOnly = true)
    public TipoConcepto obtenerPorId(Long id) {
        return tipoConceptoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoConcepto", id));
    }
}