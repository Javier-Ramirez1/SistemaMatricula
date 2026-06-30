package pe.edu.unjfsc.sistemamatricula.entity.enums;

/**
 * Borrado lógico universal. Se mapea contra el TINYINT de la BD
 * con @Convert (ver EstadoRegistroConverter) o directamente como
 * Byte en las entidades más simples — aquí se usa como Byte 1/0
 * en la mayoría de entities por simplicidad y porque así está la BD.
 *
 * Se deja este enum disponible para los Services/DTOs que prefieran
 * trabajar con texto legible (ACTIVO/INACTIVO) en vez de 1/0.
 */
public enum EstadoRegistro {
    ACTIVO((byte) 1),
    INACTIVO((byte) 0);

    private final byte valor;

    EstadoRegistro(byte valor) { this.valor = valor; }

    public byte getValor() { return valor; }

    public static EstadoRegistro desde(byte valor) {
        return valor == 1 ? ACTIVO : INACTIVO;
    }
}