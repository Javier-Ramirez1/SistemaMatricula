package pe.edu.unjfsc.sistemamatricula.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utilidades numéricas para montos (S/), evita errores de
 * redondeo de punto flotante usando siempre BigDecimal.
 */
public final class NumeroUtil {

    private NumeroUtil() {}

    public static BigDecimal redondear(BigDecimal monto) {
        if (monto == null) return BigDecimal.ZERO;
        return monto.setScale(2, RoundingMode.HALF_UP);
    }

    public static boolean esMontoValido(BigDecimal monto) {
        return monto != null && monto.compareTo(BigDecimal.ZERO) > 0;
    }

    /** Genera el correlativo formateado: BOL-2026-000125 */
    public static String formatearCorrelativo(String prefijo, int anio, long numero) {
        return String.format("%s-%d-%06d", prefijo, anio, numero);
    }

    public static boolean esSoloNumeros(String texto) {
        return texto != null && texto.matches("\\d+");
    }
}