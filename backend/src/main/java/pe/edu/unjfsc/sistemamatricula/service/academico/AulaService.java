package pe.edu.unjfsc.sistemamatricula.service.academico;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.dto.academico.AulaRequest;
import pe.edu.unjfsc.sistemamatricula.dto.academico.AulaResponse;
import pe.edu.unjfsc.sistemamatricula.entity.academico.*;
import pe.edu.unjfsc.sistemamatricula.exception.BusinessException;
import pe.edu.unjfsc.sistemamatricula.exception.ResourceNotFoundException;
import pe.edu.unjfsc.sistemamatricula.mapper.AulaMapper;
import pe.edu.unjfsc.sistemamatricula.repository.*;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Aula = combinación única año + nivel + grado + sección, con
 * capacidad controlada por el parámetro MAX_ALUMNOS_POR_AULA
 * (tabla parametro), tal como se ve en el modal "Buscar Aula"
 * del boceto (capacidad máxima / vacantes disponibles).
 */
@Service
@RequiredArgsConstructor
public class AulaService {

    private final AulaRepository aulaRepository;
    private final AnioAcademicoRepository anioAcademicoRepository;
    private final NivelRepository nivelRepository;
    private final GradoRepository gradoRepository;
    private final SeccionRepository seccionRepository;
    private final ParametroRepository parametroRepository;
    private final AulaMapper aulaMapper;

    @Transactional(readOnly = true)
    public List<AulaResponse> listarPorAnio(Long idAnioAcademico) {
        int capacidad = obtenerCapacidadMaxima();
        return aulaRepository.findByAnioAcademicoIdAndEstado(idAnioAcademico, Constantes.ACTIVO).stream()
                .map(a -> aulaMapper.toResponse(a, capacidad))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Aula obtenerPorId(Long id) {
        return aulaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aula", id));
    }

    @Transactional
    public AulaResponse crear(AulaRequest request) {
        if (aulaRepository.findByAnioAcademicoIdAndNivelIdAndGradoIdAndSeccionId(
                request.getIdAnioAcademico(), request.getIdNivel(), request.getIdGrado(), request.getIdSeccion()
        ).isPresent()) {
            throw new BusinessException("Ya existe un aula con esa combinación de año, nivel, grado y sección.");
        }

        AnioAcademico anio = anioAcademicoRepository.findById(request.getIdAnioAcademico())
                .orElseThrow(() -> new ResourceNotFoundException("AnioAcademico", request.getIdAnioAcademico()));
        Nivel nivel = nivelRepository.findById(request.getIdNivel())
                .orElseThrow(() -> new ResourceNotFoundException("Nivel", request.getIdNivel()));
        Grado grado = gradoRepository.findById(request.getIdGrado())
                .orElseThrow(() -> new ResourceNotFoundException("Grado", request.getIdGrado()));
        Seccion seccion = seccionRepository.findById(request.getIdSeccion())
                .orElseThrow(() -> new ResourceNotFoundException("Seccion", request.getIdSeccion()));

        Aula aula = new Aula();
        aula.setAnioAcademico(anio);
        aula.setNivel(nivel);
        aula.setGrado(grado);
        aula.setSeccion(seccion);
        aula.setCantidadAlumnos(0);
        aula.setEstado(Constantes.ACTIVO);

        return aulaMapper.toResponse(aulaRepository.save(aula), obtenerCapacidadMaxima());
    }

    public int obtenerCapacidadMaxima() {
        return parametroRepository.findByCodigo(Constantes.PARAM_MAX_ALUMNOS_AULA)
                .map(p -> Integer.parseInt(p.getValor()))
                .orElse(35); // valor por defecto si el parámetro no existe en BD
    }
}