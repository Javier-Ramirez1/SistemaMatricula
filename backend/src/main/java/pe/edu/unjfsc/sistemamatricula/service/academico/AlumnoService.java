package pe.edu.unjfsc.sistemamatricula.service.academico;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.dto.academico.AlumnoRequest;
import pe.edu.unjfsc.sistemamatricula.dto.academico.AlumnoResponse;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Alumno;
import pe.edu.unjfsc.sistemamatricula.entity.academico.TipoDocumento;
import pe.edu.unjfsc.sistemamatricula.exception.BusinessException;
import pe.edu.unjfsc.sistemamatricula.exception.ResourceNotFoundException;
import pe.edu.unjfsc.sistemamatricula.mapper.AlumnoMapper;
import pe.edu.unjfsc.sistemamatricula.repository.AlumnoRepository;
import pe.edu.unjfsc.sistemamatricula.repository.TipoDocumentoRepository;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;
import pe.edu.unjfsc.sistemamatricula.util.FechaUtil;
import pe.edu.unjfsc.sistemamatricula.validation.DocumentoValidator;

import java.util.List;
import java.util.stream.Collectors;

/** Mantenimiento de alumnos — el "cliente" del sistema (modal "Buscar Alumno" del boceto). */
@Service
@RequiredArgsConstructor
public class AlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final AlumnoMapper alumnoMapper;
    private final DocumentoValidator documentoValidator;

    @Transactional(readOnly = true)
    public List<AlumnoResponse> listarTodos() {
        return alumnoRepository.findByEstado(Constantes.ACTIVO).stream()
                .map(alumnoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AlumnoResponse> buscarPorNombreOApellido(String texto) {
        return alumnoRepository
                .findByNombresContainingIgnoreCaseOrApellidoPaternoContainingIgnoreCaseOrApellidoMaternoContainingIgnoreCase(
                        texto, texto, texto)
                .stream()
                .map(alumnoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Alumno obtenerPorId(Long id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno", id));
    }

    @Transactional
    public AlumnoResponse crear(AlumnoRequest request) {
        TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(request.getIdTipoDocumento())
                .orElseThrow(() -> new ResourceNotFoundException("TipoDocumento", request.getIdTipoDocumento()));

        if (!documentoValidator.esValidoSegunTipo(tipoDocumento.getCodigo(), request.getNumeroDocumento())) {
            throw new BusinessException("El número de documento no es válido para el tipo " + tipoDocumento.getCodigo());
        }

        if (alumnoRepository.existsByTipoDocumentoIdAndNumeroDocumento(
                request.getIdTipoDocumento(), request.getNumeroDocumento())) {
            throw new BusinessException("Ya existe un alumno registrado con ese tipo y número de documento.");
        }

        if (!FechaUtil.esFechaNacimientoValida(request.getFechaNacimiento())) {
            throw new BusinessException("La fecha de nacimiento no es válida.");
        }

        Alumno alumno = new Alumno();
        alumno.setTipoDocumento(tipoDocumento);
        alumno.setNumeroDocumento(request.getNumeroDocumento());
        alumno.setApellidoPaterno(request.getApellidoPaterno());
        alumno.setApellidoMaterno(request.getApellidoMaterno());
        alumno.setNombres(request.getNombres());
        alumno.setFechaNacimiento(request.getFechaNacimiento());
        alumno.setEstado(Constantes.ACTIVO);

        return alumnoMapper.toResponse(alumnoRepository.save(alumno));
    }

    @Transactional
    public AlumnoResponse actualizar(Long id, AlumnoRequest request) {
        Alumno alumno = obtenerPorId(id);

        alumno.setApellidoPaterno(request.getApellidoPaterno());
        alumno.setApellidoMaterno(request.getApellidoMaterno());
        alumno.setNombres(request.getNombres());
        alumno.setFechaNacimiento(request.getFechaNacimiento());

        return alumnoMapper.toResponse(alumnoRepository.save(alumno));
    }

    @Transactional
    public void eliminarLogico(Long id) {
        Alumno alumno = obtenerPorId(id);
        alumno.setEstado(Constantes.INACTIVO);
        alumnoRepository.save(alumno);
    }
}