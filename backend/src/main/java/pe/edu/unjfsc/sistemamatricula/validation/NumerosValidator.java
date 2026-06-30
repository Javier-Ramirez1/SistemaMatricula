package pe.edu.unjfsc.sistemamatricula.validation;

import org.springframework.stereotype.Component;
import pe.edu.unjfsc.sistemamatricula.util.ValidacionUtil;

/** Validador para campos numéricos puros (documentos, teléfonos, cantidades). */
@Component
public class NumerosValidator {

    public boolean esValido(String texto) {
        return ValidacionUtil.esSoloNumeros(texto);
    }
}