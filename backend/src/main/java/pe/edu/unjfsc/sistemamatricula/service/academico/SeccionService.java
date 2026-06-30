package pe.edu.unjfsc.sistemamatricula.service.academico;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Seccion;
import pe.edu.unjfsc.sistemamatricula.exception.ResourceNotFoundException;
import pe.edu.unjfsc.sistemamatricula.repository.SeccionRepository;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeccionService {

    private final SeccionRepository seccionRepository;

    @Transactional(readOnly = true)
    public List<Seccion> listarActivas() {
        return seccionRepository.findByEstado(Constantes.ACTIVO);
    }

    @Transactional(readOnly = true)
    public Seccion obtenerPorId(Long id) {
        return seccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seccion", id));
    }
}