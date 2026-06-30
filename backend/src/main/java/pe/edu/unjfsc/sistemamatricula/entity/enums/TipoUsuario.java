package pe.edu.unjfsc.sistemamatricula.entity.enums;

/**
 * Espejo de los nombres de rol en BD (tabla rol.nombre).
 * Útil para comparar roles en código sin usar strings sueltos
 * ("SUPERUSUARIO" tipeado a mano en 10 lugares distintos).
 */
public enum TipoUsuario {
    SUPERUSUARIO,
    DIRECTOR,
    SECRETARIA,
    DOCENTE
}