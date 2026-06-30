package pe.edu.unjfsc.sistemamatricula.mapper;

import org.springframework.stereotype.Component;
import pe.edu.unjfsc.sistemamatricula.dto.academico.AlumnoResponse;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Alumno;
import pe.edu.unjfsc.sistemamatricula.util.FechaUtil;

@Component
public class AlumnoMapper {

    public AlumnoResponse toResponse(Alumno alumno) {
        if (alumno == null) return null;
        return new AlumnoResponse(
                alumno.getId(),
                alumno.getTipoDocumento() != null ? alumno.getTipoDocumento().getCodigo() : null,
                alumno.getNumeroDocumento(),
                alumno.getApellidoPaterno(),
                alumno.getApellidoMaterno(),
                alumno.getNombres(),
                alumno.getNombreCompleto(),
                alumno.getFechaNacimiento(),
                FechaUtil.calcularEdad(alumno.getFechaNacimiento()),
                alumno.getEstado(),
                alumno.getVersion()
        );
    }
}