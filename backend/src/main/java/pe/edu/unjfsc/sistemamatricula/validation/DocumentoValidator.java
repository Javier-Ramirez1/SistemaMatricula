package pe.edu.unjfsc.sistemamatricula.validation;

import org.springframework.stereotype.Component;
import pe.edu.unjfsc.sistemamatricula.util.ValidacionUtil;

/**
 * Validador específico de números de documento de identidad
 * (DNI, CE, Pasaporte) — invocado manualmente desde los Services
 * cuando la regla depende del tipo de documento elegido (ej. DNI
 * exige exactamente 8 dígitos, CE/Pasaporte son alfanuméricos).
 */
@Component
public class DocumentoValidator {

    public boolean esValidoSegunTipo(String codigoTipoDocumento, String numeroDocumento) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) return false;

        return switch (codigoTipoDocumento) {
            case "DNI" -> ValidacionUtil.esDniValido(numeroDocumento);
            case "CE", "PAS" -> numeroDocumento.matches("^[0-9A-Za-z]{6,20}$");
            default -> false;
        };
    }
}
