package pe.edu.unjfsc.sistemamatricula.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * DTOs del módulo de seguridad: login, usuarios, roles, permisos.
 * Agrupados en un solo archivo (como ya estaba planteado el
 * proyecto) para no fragmentar en 15 archivos de 10 líneas.
 */
public class seguridad {

    @Data
    public static class LoginRequest {
        @NotBlank(message = "El usuario es obligatorio")
        private String username;

        @NotBlank(message = "La contraseña es obligatoria")
        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginResponse {
        private String token;
        private String username;
        private String nombreCompleto;
        private Set<String> roles;
        private boolean esSuperusuario;
    }

    @Data
    public static class CambiarPasswordRequest {
        @NotBlank(message = "La contraseña actual es obligatoria")
        private String passwordActual;

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
        private String passwordNueva;
    }

    @Data
    public static class UsuarioRequest {
        @NotBlank(message = "El username es obligatorio")
        @Pattern(regexp = "^[A-Za-z0-9._]{4,50}$", message = "Username inválido (4-50 caracteres, sin espacios)")
        private String username;

        // Solo obligatorio al crear; en edición puede venir null (no se cambia)
        private String password;

        @NotBlank(message = "El nombre completo es obligatorio")
        @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]+$", message = "El nombre solo debe contener letras")
        private String nombreCompleto;

        @Email(message = "Email inválido")
        private String email;

        @NotNull(message = "El tipo de documento es obligatorio")
        private Long idTipoDocumento;

        @NotBlank(message = "El número de documento es obligatorio")
        @Pattern(regexp = "^[0-9]{8,20}$", message = "El número de documento solo debe contener números")
        private String numeroDocumento;

        @NotEmpty(message = "Debe asignarse al menos un rol")
        private List<Long> idsRoles;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UsuarioResponse {
        private Long id;
        private String username;
        private String nombreCompleto;
        private String email;
        private String tipoDocumento;
        private String numeroDocumento;
        private boolean esSuperusuario;
        private List<String> roles;
        private Byte estado;
        private Long version;
        private LocalDateTime fechaInsert;
    }

    @Data
    public static class RolRequest {
        @NotBlank(message = "El nombre del rol es obligatorio")
        private String nombre;

        private String descripcion;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RolResponse {
        private Long id;
        private String nombre;
        private String descripcion;
        private Byte estado;
        private Long version;
        private List<PermisoResponse> permisos;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PermisoResponse {
        private Long id;
        private String modulo;
        private String submodulo;
        private String accion;
        private String descripcion;
    }

    /**
     * Request del modal "Asignar Permisos - Secretaria" del boceto:
     * lista de checkboxes (idPermiso, marcado true/false) + botón Aplicar.
     */
    @Data
    public static class AsignarPermisosRequest {
        @NotNull(message = "El rol es obligatorio")
        private Long idRol;

        @NotEmpty(message = "La lista de permisos es obligatoria")
        private List<Long> idsPermisosSeleccionados;
    }
}