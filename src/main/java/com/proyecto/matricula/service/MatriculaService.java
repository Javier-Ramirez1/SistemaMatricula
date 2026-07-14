package com.proyecto.matricula.service;

import com.proyecto.matricula.entity.Alumno;
import com.proyecto.matricula.entity.Aula;
import com.proyecto.matricula.entity.Concepto;
import com.proyecto.matricula.entity.Cuota;
import com.proyecto.matricula.entity.Matricula;
import com.proyecto.matricula.repository.AlumnoRepository;
import com.proyecto.matricula.repository.AulaRepository;
import com.proyecto.matricula.repository.ConceptoRepository;
import com.proyecto.matricula.repository.CuotaRepository;
import com.proyecto.matricula.repository.MatriculaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlumnoRepository alumnoRepository;
    private final AulaRepository aulaRepository;
    private final ConceptoRepository conceptoRepository;
    private final CuotaRepository cuotaRepository;
    private final AuditoriaService auditoriaService;

    public MatriculaService(MatriculaRepository matriculaRepository, AlumnoRepository alumnoRepository,
                            AulaRepository aulaRepository, ConceptoRepository conceptoRepository,
                            CuotaRepository cuotaRepository, AuditoriaService auditoriaService) {
        this.matriculaRepository = matriculaRepository;
        this.alumnoRepository = alumnoRepository;
        this.aulaRepository = aulaRepository;
        this.conceptoRepository = conceptoRepository;
        this.cuotaRepository = cuotaRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public Matricula registrarMatricula(Integer codAlumno, Integer codAula) {
        // 1. Validar existencia
        Alumno alumno = alumnoRepository.findByCodAlumnoAndEstadoTrue(codAlumno)
                .orElseThrow(() -> new IllegalArgumentException("El Alumno no existe o está inactivo."));
        Aula aula = aulaRepository.findByCodAulaAndEstadoTrue(codAula)
                .orElseThrow(() -> new IllegalArgumentException("El Aula no existe o está inactiva."));

        Integer codAnio = aula.getAnioAcademico().getCodAnioAcademico();

        // 2. Validar que el alumno no esté matriculado en el mismo año
        boolean yaMatriculado = matriculaRepository.existsByAnioAcademicoCodAnioAcademicoAndAlumnoCodAlumnoAndEstadoTrue(codAnio, codAlumno);
        if (yaMatriculado) {
            throw new IllegalStateException("El Alumno ya se encuentra matriculado en este año académico.");
        }

        // 2b. Validar que el alumno no tenga deudas pendientes
        List<Cuota> deudasPendientes = cuotaRepository.findAllPendingCuotasByAlumno(codAlumno);
        if (!deudasPendientes.isEmpty()) {
            throw new IllegalStateException("El alumno registra deudas pendientes en su historial de pagos. Debe cancelar sus cuotas previas antes de matricularse.");
        }

        // 3. Validar vacantes del aula
        if (aula.getVacantesDisponibles() <= 0) {
            throw new IllegalStateException("El Aula seleccionada no cuenta con vacantes disponibles.");
        }

        // 4. Validar existencia de conceptos activos para ese año
        List<Concepto> conceptos = conceptoRepository.findByAnioAcademicoCodAnioAcademicoAndEstadoTrueOrderByOrdenPagoAsc(codAnio);
        if (conceptos.isEmpty()) {
            throw new IllegalStateException("No se pueden generar matrículas porque no hay conceptos de pago configurados para este año.");
        }

        // 5. Registrar Matrícula
        Matricula matricula = new Matricula();
        matricula.setAlumno(alumno);
        matricula.setAula(aula);
        matricula.setAnioAcademico(aula.getAnioAcademico());
        matricula.setEstado(true);
        matricula = matriculaRepository.save(matricula);

        // 6. Generar cuotas automáticas basadas en los conceptos obligatorios
        for (Concepto concepto : conceptos) {
            if (concepto.getObligatorio()) {
                Cuota cuota = new Cuota();
                cuota.setMatricula(matricula);
                cuota.setConcepto(concepto);
                cuota.setMontoOriginal(concepto.getMonto());
                cuota.setMontoPendiente(concepto.getMonto());
                cuota.setOrdenPago(concepto.getOrdenPago());
                cuota.setPagado(false);
                cuota.setEstado(true);
                cuotaRepository.save(cuota);
            }
        }

        // 7. Decrementar vacantes del Aula
        aula.setVacantesDisponibles((short) (aula.getVacantesDisponibles() - 1));
        aulaRepository.save(aula);

        // 8. Registrar auditoría del proceso completo
        auditoriaService.registrar(
                "MATRÍCULAS",
                "matricula",
                "INSERT",
                matricula.getCodMatricula(),
                null,
                "Matrícula registrada exitosamente para el alumno " + alumno.getPersona().getNumeroDocumento() + " en el aula " + aula.getCodAula()
        );

        return matricula;
    }
}
