package pe.edu.unjfsc.sistemamatricula.service.academico;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.dto.academico.AnioAcademicoRequest;
import pe.edu.unjfsc.sistemamatricula.entity.academico.AnioAcademico;
import pe.edu.unjfsc.sistemamatricula.exception.BusinessException;
import pe.edu.unjfsc.sistemamatricula.exception.ResourceNotFoundException;
import pe.edu.unjfsc.sistemamatricula.repository.AnioAcademicoRepository;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

import java.util.List;

/**
 * El año académico es la pieza central de trazabilidad: aula,
 * concepto, matrícula y correlativo dependen de él para mantener
 * históricos separados año a año (2025, 2026, 2027...).
 */
@Service
@RequiredArgsConstructor
public class AnioAcademicoService {

    private final AnioAcademicoRepository anioAcademicoRepository;

    @Transactional(readOnly = true)
    public List<AnioAcademico> listarTodos() {
        return anioAcademicoRepository.findByEstadoOrderByAnioDesc(Constantes.ACTIVO);
    }

    @Transactional(readOnly = true)
    public AnioAcademico obtenerActivo() {
        return anioAcademicoRepository.findByActivo(Constantes.ACTIVO)
                .orElseThrow(() -> new BusinessException("No hay ningún año académico marcado como activo."));
    }

    @Transactional(readOnly = true)
    public AnioAcademico obtenerPorId(Long id) {
        return anioAcademicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnioAcademico", id));
    }

    @Transactional
    public AnioAcademico crear(AnioAcademicoRequest request) {
        if (anioAcademicoRepository.findByAnio(request.getAnio()).isPresent()) {
            throw new BusinessException("El año académico " + request.getAnio() + " ya existe.");
        }
        AnioAcademico anio = new AnioAcademico();
        anio.setAnio(request.getAnio());
        anio.setActivo((byte) 0);
        anio.setEstado(Constantes.ACTIVO);
        return anioAcademicoRepository.save(anio);
    }

    /** Marca un año como activo y desactiva el anterior — solo uno puede estar activo a la vez. */
    @Transactional
    public AnioAcademico activar(Long id) {
        AnioAcademico nuevoActivo = obtenerPorId(id);

        anioAcademicoRepository.findByActivo(Constantes.ACTIVO).ifPresent(actual -> {
            actual.setActivo((byte) 0);
            anioAcademicoRepository.save(actual);
        });

        nuevoActivo.setActivo((byte) 1);
        return anioAcademicoRepository.save(nuevoActivo);
    }
}