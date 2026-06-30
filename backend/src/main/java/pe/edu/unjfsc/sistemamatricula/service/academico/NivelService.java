package pe.edu.unjfsc.sistemamatricula.service.academico;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Nivel;
import pe.edu.unjfsc.sistemamatricula.exception.ResourceNotFoundException;
import pe.edu.unjfsc.sistemamatricula.repository.NivelRepository;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NivelService {

    private final NivelRepository nivelRepository;

    @Transactional(readOnly = true)
    public List<Nivel> listarActivos() {
        return nivelRepository.findByEstado(Constantes.ACTIVO);
    }

    @Transactional(readOnly = true)
    public Nivel obtenerPorId(Long id) {
        return nivelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nivel", id));
    }
}