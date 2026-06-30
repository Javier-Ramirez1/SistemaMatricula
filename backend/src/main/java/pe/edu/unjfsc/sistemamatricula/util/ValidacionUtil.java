package pe.edu.unjfsc.sistemamatricula.util;

import java.util.regex.Pattern;

/**
 * Validaciones genéricas de texto reutilizadas por DocumentoValidator,
 * LetrasValidator y NumerosValidator (capa @Valid de los DTOs), y
 * también invocables directamente desde los Services como segunda
 * barrera de defensa (defensa en profundidad: front valida, DTO
 * valida con @Valid, y el Service vuelve a validar antes de persistir).
 */
public final class ValidacionUtil {

    private ValidacionUtil() {}

    // Solo letras y espacios (incluye tildes y ñ) — nombres, apellidos
    private static final Pattern SOLO_LETRAS = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]+$");

    // Solo dígitos — números de documento, teléfonos
    private static final Pattern SOLO_NUMEROS = Pattern.compile("^[0-9]+$");

    // DNI peruano: exactamente 8 dígitos
    private static final Pattern DNI = Pattern.compile("^[0-9]{8}$");

    // Username: letras, números, punto y guion bajo, 4-50 caracteres
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9._]{4,50}$");

    public static boolean esSoloLetras(String texto) {
        return texto != null && !texto.isBlank() && SOLO_LETRAS.matcher(texto.trim()).matches();
    }

    public static boolean esSoloNumeros(String texto) {
        return texto != null && SOLO_NUMEROS.matcher(texto).matches();
    }

    public static boolean esDniValido(String texto) {
        return texto != null && DNI.matcher(texto).matches();
    }

    public static boolean esUsernameValido(String texto) {
        return texto != null && USERNAME.matcher(texto).matches();
    }

    public static boolean esVacioONulo(String texto) {
        return texto == null || texto.isBlank();
    }

    /** Limpia espacios extra y normaliza a mayúsculas (útil para códigos y nombres de catálogo). */
    public static String normalizarMayusculas(String texto) {
        return texto == null ? null : texto.trim().toUpperCase();
    }
}