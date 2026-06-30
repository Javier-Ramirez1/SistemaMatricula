package pe.edu.unjfsc.sistemamatricula.validation;

import org.springframework.stereotype.Component;
import pe.edu.unjfsc.sistemamatricula.util.ValidacionUtil;

/** Validador para campos que solo deben aceptar letras (nombres, apellidos, descripciones de catálogo). */
@Component
public class LetrasValidator {

    public boolean esValido(String texto) {
        return ValidacionUtil.esSoloLetras(texto);
    }
}