package pe.edu.unjfsc.sistemamatricula.service.financiero;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.MatriculaResponse;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Cuota;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Matricula;
import pe.edu.unjfsc.sistemamatricula.exception.ResourceNotFoundException;
import pe.edu.unjfsc.sistemamatricula.mapper.MatriculaMapper;
import pe.edu.unjfsc.sistemamatricula.repository.CuotaRepository;
import pe.edu.unjfsc.sistemamatricula.repository.MatriculaRepository;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Capa de CONSULTA de matrículas. El proceso de CREACIÓN (matricular,
 * que toca varias tablas con reglas críticas de cupo y concurrencia)
 * vive deliberadamente en MatriculaProcess, no aquí — separa
 * lectura simple de transacción de negocio compleja.
 */
@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final CuotaRepository cuotaRepository;
    private final MatriculaMapper matriculaMapper;

    @Transactional(readOnly = true)
    public List<MatriculaResponse> listarPorAnio(Long idAnioAcademico) {
        return matriculaRepository.findByAnioAcademicoIdAndEstado(idAnioAcademico, Constantes.ACTIVO).stream()
                .map(m -> matriculaMapper.toResponse(m, cuotaRepository.findByMatriculaIdOrderByOrdenCobro(m.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MatriculaResponse obtenerPorAlumnoYAnio(Long idAlumno, Long idAnioAcademico) {
        Matricula matricula = matriculaRepository.findByAlumnoIdAndAnioAcademicoId(idAlumno, idAnioAcademico)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El alumno no tiene una matrícula registrada en el año académico seleccionado."));
        List<Cuota> cuotas = cuotaRepository.findByMatriculaIdOrderByOrdenCobro(matricula.getId());
        return matriculaMapper.toResponse(matricula, cuotas);
    }

    @Transactional(readOnly = true)
    public Matricula obtenerEntidadPorId(Long id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matricula", id));
    }
}