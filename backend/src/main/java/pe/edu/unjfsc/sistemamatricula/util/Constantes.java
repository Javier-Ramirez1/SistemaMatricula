package pe.edu.unjfsc.sistemamatricula.util;

/**
 * Constantes globales del sistema, evita strings sueltos repetidos
 * en Services, Controllers y Process.
 */
public final class Constantes {

    private Constantes() {}

    // Roles (deben coincidir con rol.nombre en BD)
    public static final String ROL_SUPERUSUARIO = "SUPERUSUARIO";
    public static final String ROL_DIRECTOR = "DIRECTOR";
    public static final String ROL_SECRETARIA = "SECRETARIA";
    public static final String ROL_DOCENTE = "DOCENTE";

    // Estado de registro (borrado lógico)
    public static final byte ACTIVO = 1;
    public static final byte INACTIVO = 0;

    // Parámetros del sistema (tabla parametro.codigo)
    public static final String PARAM_MAX_ALUMNOS_AULA = "MAX_ALUMNOS_POR_AULA";
    public static final String PARAM_HABILITAR_PAGOS_ONLINE = "HABILITAR_PAGOS_ONLINE";

    // Prefijo de correlativo de boletas
    public static final String PREFIJO_BOLETA_DEFAULT = "BOL";

    // Usuario de sistema (procesos batch / seed sin sesión)
    public static final String USUARIO_SISTEMA = "SISTEMA";

    // Headers / JWT
    public static final String HEADER_AUTORIZACION = "Authorization";
    public static final String PREFIJO_BEARER = "Bearer ";
}