package pe.edu.unjfsc.sistemamatricula.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.MatricularRequest;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.MatriculaResponse;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Alumno;
import pe.edu.unjfsc.sistemamatricula.entity.academico.AnioAcademico;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Aula;
import pe.edu.unjfsc.sistemamatricula.entity.enums.EstadoMatricula;
import pe.edu.unjfsc.sistemamatricula.entity.enums.EstadoPago;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Concepto;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Cuota;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Matricula;
import pe.edu.unjfsc.sistemamatricula.exception.BusinessException;
import pe.edu.unjfsc.sistemamatricula.exception.ResourceNotFoundException;
import pe.edu.unjfsc.sistemamatricula.mapper.MatriculaMapper;
import pe.edu.unjfsc.sistemamatricula.repository.*;
import pe.edu.unjfsc.sistemamatricula.service.academico.AulaService;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Proceso de MATRICULAR a un alumno — toca varias tablas con reglas
 * críticas de cupo y concurrencia, por eso vive separado de
 * MatriculaService (que solo hace consultas).
 *
 * Pasos (todo dentro de una sola transacción ACID):
 *  1) el alumno no debe tener ya una matrícula ACTIVA en ese año
 *  2) se bloquea el aula con SELECT ... FOR UPDATE (ver AulaRepository)
 *     para que dos matrículas simultáneas en la última vacante no
 *     pasen ambas el chequeo de cupo con datos obsoletos
 *  3) se valida cupo contra el parámetro MAX_ALUMNOS_POR_AULA
 *  4) se incrementa aula.cantidadAlumnos
 *  5) se crea la Matricula
 *  6) se generan las Cuotas a partir de los Conceptos vigentes del
 *     año (el monto se COPIA al momento de matricular, ver Cuota.java)
 */
@Component
@RequiredArgsConstructor
public class MatriculaProcess {

    private final AlumnoRepository alumnoRepository;
    private final AnioAcademicoRepository anioAcademicoRepository;
    private final AulaRepository aulaRepository;
    private final AulaService aulaService;
    private final MatriculaRepository matriculaRepository;
    private final ConceptoRepository conceptoRepository;
    private final CuotaRepository cuotaRepository;
    private final MatriculaMapper matriculaMapper;

    @Transactional
    public MatriculaResponse matricular(MatricularRequest request) {

        Alumno alumno = alumnoRepository.findById(request.getIdAlumno())
                .orElseThrow(() -> new ResourceNotFoundException("Alumno", request.getIdAlumno()));

        AnioAcademico anioAcademico = anioAcademicoRepository.findById(request.getIdAnioAcademico())
                .orElseThrow(() -> new ResourceNotFoundException("AnioAcademico", request.getIdAnioAcademico()));

        if (matriculaRepository.existsByAlumnoIdAndAnioAcademicoIdAndEstadoMatricula(
                request.getIdAlumno(), request.getIdAnioAcademico(), EstadoMatricula.ACTIVA)) {
            throw new BusinessException("El alumno ya tiene una matrícula activa en el año académico seleccionado.");
        }

        // Bloqueo pesimista del aula: imprescindible para el chequeo de cupo seguro.
        Aula aula = aulaRepository.findByIdForUpdate(request.getIdAula())
                .orElseThrow(() -> new ResourceNotFoundException("Aula", request.getIdAula()));

        if (!aula.getAnioAcademico().getId().equals(request.getIdAnioAcademico())) {
            throw new BusinessException("El aula seleccionada no pertenece al año académico indicado.");
        }

        int capacidadMaxima = aulaService.obtenerCapacidadMaxima();
        if (aula.getCantidadAlumnos() >= capacidadMaxima) {
            throw new BusinessException("El aula seleccionada ya no tiene vacantes disponibles.");
        }

        List<Concepto> conceptos = conceptoRepository
                .findByAnioAcademicoIdAndEstadoOrderByOrdenCobro(request.getIdAnioAcademico(), Constantes.ACTIVO);

        if (conceptos.isEmpty()) {
            throw new BusinessException("El año académico seleccionado no tiene conceptos de pago configurados.");
        }

        aula.setCantidadAlumnos(aula.getCantidadAlumnos() + 1);
        aulaRepository.save(aula);

        Matricula matricula = new Matricula();
        matricula.setAnioAcademico(anioAcademico);
        matricula.setAlumno(alumno);
        matricula.setAula(aula);
        matricula.setFechaMatricula(LocalDate.now());
        matricula.setEstadoMatricula(EstadoMatricula.ACTIVA);
        matricula.setEstado(Constantes.ACTIVO);
        matricula = matriculaRepository.save(matricula);

        List<Cuota> cuotas = new ArrayList<>();
        for (Concepto concepto : conceptos) {
            Cuota cuota = new Cuota();
            cuota.setMatricula(matricula);
            cuota.setConcepto(concepto);
            cuota.setMonto(concepto.getMonto());
            cuota.setOrdenCobro(concepto.getOrdenCobro());
            cuota.setEstadoPago(EstadoPago.PENDIENTE);
            cuota.setEstado(Constantes.ACTIVO);
            cuotas.add(cuota);
        }
        cuotas = cuotaRepository.saveAll(cuotas);

        return matriculaMapper.toResponse(matricula, cuotas);
    }
}