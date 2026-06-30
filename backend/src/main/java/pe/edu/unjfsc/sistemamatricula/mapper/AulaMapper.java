package pe.edu.unjfsc.sistemamatricula.mapper;

import org.springframework.stereotype.Component;
import pe.edu.unjfsc.sistemamatricula.dto.academico.AulaResponse;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Aula;

@Component
public class AulaMapper {

    public AulaResponse toResponse(Aula aula, int capacidadMaxima) {
        if (aula == null) return null;
        int ocupados = aula.getCantidadAlumnos() == null ? 0 : aula.getCantidadAlumnos();
        return new AulaResponse(
                aula.getId(),
                aula.getAnioAcademico().getAnio(),
                aula.getNivel().getNombre(),
                aula.getGrado().getNombre(),
                aula.getSeccion().getNombre(),
                ocupados,
                capacidadMaxima,
                Math.max(0, capacidadMaxima - ocupados),
                aula.getEstado(),
                aula.getVersion()
        );
    }
}