package pe.edu.unjfsc.sistemamatricula.mapper;

import org.springframework.stereotype.Component;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.CuotaResponse;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.MatriculaResponse;
import pe.edu.unjfsc.sistemamatricula.entity.enums.EstadoPago;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Cuota;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Matricula;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MatriculaMapper {

    public MatriculaResponse toResponse(Matricula matricula, List<Cuota> cuotas) {
        if (matricula == null) return null;

        List<Cuota> ordenadas = cuotas.stream()
                .sorted(Comparator.comparing(Cuota::getOrdenCobro))
                .collect(Collectors.toList());

        // Determina hasta qué orden está pagado de forma continua, para
        // habilitar en el frontend solo la próxima cuota pagable (regla
        // de "no se puede pagar la cuota 3 sin pagar la 1").
        int menorOrdenPendiente = ordenadas.stream()
                .filter(c -> c.getEstadoPago() == EstadoPago.PENDIENTE)
                .map(Cuota::getOrdenCobro)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);

        List<CuotaResponse> cuotasResponse = ordenadas.stream()
                .map(c -> new CuotaResponse(
                        c.getId(),
                        c.getConcepto().getNombre(),
                        c.getMonto(),
                        c.getOrdenCobro(),
                        c.getEstadoPago().name(),
                        c.getEstadoPago() == EstadoPago.PENDIENTE && c.getOrdenCobro().equals(menorOrdenPendiente)
                ))
                .collect(Collectors.toList());

        return new MatriculaResponse(
                matricula.getId(),
                matricula.getAlumno().getNombreCompleto(),
                matricula.getAula().getNivel().getNombre() + " " + matricula.getAula().getGrado().getNombre()
                        + " \"" + matricula.getAula().getSeccion().getNombre() + "\"",
                matricula.getAnioAcademico().getAnio(),
                matricula.getFechaMatricula(),
                matricula.getEstadoMatricula().name(),
                matricula.getVersion(),
                cuotasResponse
        );
    }
}