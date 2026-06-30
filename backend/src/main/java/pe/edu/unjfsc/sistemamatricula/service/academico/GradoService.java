package pe.edu.unjfsc.sistemamatricula.service.academico;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Grado;
import pe.edu.unjfsc.sistemamatricula.exception.ResourceNotFoundException;
import pe.edu.unjfsc.sistemamatricula.repository.GradoRepository;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradoService {

    private final GradoRepository gradoRepository;

    @Transactional(readOnly = true)
    public List<Grado> listarPorNivel(Long idNivel) {
        return gradoRepository.findByNivelIdAndEstadoOrderByOrden(idNivel, Constantes.ACTIVO);
    }

    @Transactional(readOnly = true)
    public Grado obtenerPorId(Long id) {
        return gradoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grado", id));
    }
}