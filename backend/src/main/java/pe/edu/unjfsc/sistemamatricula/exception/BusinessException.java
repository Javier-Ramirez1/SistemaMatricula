package pe.edu.unjfsc.sistemamatricula.exception;

/**
 * Excepción de regla de negocio violada (ej. "no se puede pagar
 * la cuota 3 sin pagar la cuota 1 antes", "el aula ya no tiene cupo",
 * "no se puede eliminar al superusuario").
 *
 * Se lanza dentro de @Transactional en los Process/Service y
 * provoca el ROLLBACK automático de Spring, ya que es RuntimeException.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String mensaje) {
        super(mensaje);
    }

    public BusinessException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
