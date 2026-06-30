package pe.edu.unjfsc.sistemamatricula.exception;

/** Se lanza cuando un findById() no encuentra el registro (404). */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String entidad, Long id) {
        super(entidad + " con id=" + id + " no encontrado(a)");
    }

    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}