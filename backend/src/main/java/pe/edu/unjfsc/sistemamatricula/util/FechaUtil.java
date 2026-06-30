package pe.edu.unjfsc.sistemamatricula.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * Utilidades de fechas: formateo y cálculo de edad para validar
 * datos del alumno (ej. evitar fechas de nacimiento futuras o
 * absurdas en el formulario de matrícula).
 */
public final class FechaUtil {

    private FechaUtil() {}

    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String formatear(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FORMATO_FECHA);
    }

    public static String formatear(LocalDateTime fechaHora) {
        return fechaHora == null ? "" : fechaHora.format(FORMATO_FECHA_HORA);
    }

    public static int calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    /** Valida que la fecha no sea futura ni anterior a 100 años atrás (control de fechas absurdas). */
    public static boolean esFechaNacimientoValida(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return false;
        LocalDate hoy = LocalDate.now();
        LocalDate limiteInferior = hoy.minusYears(100);
        return !fechaNacimiento.isAfter(hoy) && !fechaNacimiento.isBefore(limiteInferior);
    }

    public static int anioActual() {
        return LocalDate.now().getYear();
    }
}