package pe.edu.unjfsc.sistemamatricula.validation;

import org.springframework.stereotype.Component;
import pe.edu.unjfsc.sistemamatricula.util.FechaUtil;

import java.time.LocalDate;

/** Validador de fechas para formularios (fecha de nacimiento, fecha de matrícula). */
@Component
public class FechaValidator {

    public boolean esFechaNacimientoValida(LocalDate fecha) {
        return FechaUtil.esFechaNacimientoValida(fecha);
    }

    public boolean esFechaFutura(LocalDate fecha) {
        return fecha != null && fecha.isAfter(LocalDate.now());
    }
}