package pe.edu.unjfsc.sistemamatricula.exception;

/**
 * Error de validación de datos de entrada que no alcanza a ser
 * capturado por Bean Validation (@Valid) — por ejemplo reglas
 * cruzadas entre varios campos del DTO.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String mensaje) {
        super(mensaje);
    }
}