package pe.edu.unjfsc.sistemamatricula.version;

import org.springframework.stereotype.Component;
import pe.edu.unjfsc.sistemamatricula.exception.BusinessException;

/**
 * Validación explícita de Optimistic Lock ANTES de intentar el save().
 *
 * Aunque JPA ya lanza OptimisticLockException automáticamente al
 * hacer UPDATE si la version no coincide (capturado en
 * GlobalExceptionHandler), esta clase permite dar un mensaje más
 * claro y específico al frontend ANTES de tocar la base de datos,
 * cuando el cliente envía explícitamente la versión que tenía
 * cargada en pantalla (ej. al pagar una cuota, ver PagoProcess).
 */
@Component
public class VersionValidator {

    public void validar(Long versionActual, Long versionRecibida, String entidad) {
        if (versionRecibida == null) return; // el cliente no envió control de versión, se confía en JPA
        if (!versionActual.equals(versionRecibida)) {
            throw new BusinessException(
                    "El registro de " + entidad + " fue modificado por otro usuario. " +
                            "Recarga la información antes de continuar.");
        }
    }
}