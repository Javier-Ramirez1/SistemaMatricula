package pe.edu.unjfsc.sistemamatricula.auditoria;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.Table;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pe.edu.unjfsc.sistemamatricula.entity.auditoria.Auditoria;
import pe.edu.unjfsc.sistemamatricula.repository.AuditoriaRepository;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Construye y persiste los registros de la tabla "auditoria".
 *
 * IMPORTANTE sobre el orden de guardado: la especificación JPA prohíbe
 * invocar operaciones de EntityManager/Query dentro de un callback de
 * ciclo de vida (@PostPersist/@PostUpdate/@PreRemove en AuditoriaListener).
 * Por eso este service NO guarda de inmediato: encola el registro
 * pendiente en la transacción Spring activa (TransactionSynchronizationManager)
 * y lo persiste recién en beforeCommit(), dentro de la MISMA transacción
 * física que originó el cambio (si esa transacción hace rollback, la
 * auditoría tampoco se guarda — consistencia total).
 */
@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final ObjectMapper objectMapper;

    private static final String CLAVE_SINCRONIZACION = "pe.edu.unjfsc.sistemamatricula.auditoria.pendientes";

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    /**
     * Serializa solo las columnas ESCALARES de la entidad (id, códigos,
     * montos, fechas, enums, estado, version...) y omite deliberadamente
     * relaciones @ManyToOne/@OneToMany y colecciones. Esto evita dos
     * problemas clásicos de auditar entidades JPA completas:
     *  - LazyInitializationException al tocar un proxy fuera de sesión
     *  - referencias circulares infinitas entre entidades relacionadas
     */
    public String serializar(Object entity) {
        if (entity == null) return null;
        Map<String, Object> valores = new LinkedHashMap<>();
        for (Field campo : obtenerCamposEscalares(entity.getClass())) {
            try {
                campo.setAccessible(true);
                valores.put(campo.getName(), campo.get(entity));
            } catch (IllegalAccessException ignored) {
                // campo no accesible: se omite del snapshot, no es crítico para la auditoría
            }
        }
        try {
            return objectMapper.writeValueAsString(valores);
        } catch (Exception e) {
            return null;
        }
    }

    /** Encola el registro de auditoría; el guardado real ocurre en beforeCommit() de la transacción activa. */
    public void encolar(Object entity, String accion, String datosAntes, String datosDespues) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return; // no hay transacción Spring activa (ej. arranque/tests) -> no se audita
        }

        Long idRegistro = invocarGetterLong(entity, "getId");
        if (idRegistro == null) return;

        PendienteAuditoria pendiente = new PendienteAuditoria(
                obtenerNombreTabla(entity.getClass()), idRegistro, accion,
                datosAntes, datosDespues, invocarGetterLong(entity, "getVersion"));

        registrarPendiente(pendiente);
    }

    @SuppressWarnings("unchecked")
    private void registrarPendiente(PendienteAuditoria pendiente) {
        List<PendienteAuditoria> pendientes =
                (List<PendienteAuditoria>) TransactionSynchronizationManager.getResource(CLAVE_SINCRONIZACION);

        if (pendientes == null) {
            pendientes = new ArrayList<>();
            TransactionSynchronizationManager.bindResource(CLAVE_SINCRONIZACION, pendientes);

            final List<PendienteAuditoria> listaFinal = pendientes;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    persistirPendientes(listaFinal);
                }

                @Override
                public void afterCompletion(int status) {
                    if (TransactionSynchronizationManager.hasResource(CLAVE_SINCRONIZACION)) {
                        TransactionSynchronizationManager.unbindResource(CLAVE_SINCRONIZACION);
                    }
                }
            });
        }
        pendientes.add(pendiente);
    }

    private void persistirPendientes(List<PendienteAuditoria> pendientes) {
        if (pendientes.isEmpty()) return;

        String usuario = obtenerUsuarioActual();
        String ip = obtenerIpCliente();
        String navegador = obtenerNavegador();

        for (PendienteAuditoria p : pendientes) {
            Auditoria auditoria = new Auditoria();
            auditoria.setTabla(p.tabla);
            auditoria.setIdRegistro(p.idRegistro);
            auditoria.setAccion(p.accion);
            auditoria.setDatosAntes(p.datosAntes);
            auditoria.setDatosDespues(p.datosDespues);
            auditoria.setUsuario(usuario);
            auditoria.setIpCliente(ip);
            auditoria.setNavegador(navegador);
            auditoria.setVersionEntity(p.versionEntity);
            auditoriaRepository.save(auditoria);
        }
    }

    private List<Field> obtenerCamposEscalares(Class<?> clase) {
        List<Field> resultado = new ArrayList<>();
        Class<?> actual = clase;
        while (actual != null && actual != Object.class) {
            for (Field campo : actual.getDeclaredFields()) {
                if (esTipoEscalar(campo.getType())) {
                    resultado.add(campo);
                }
            }
            actual = actual.getSuperclass();
        }
        return resultado;
    }

    private boolean esTipoEscalar(Class<?> tipo) {
        return tipo.isPrimitive()
                || tipo == String.class
                || tipo == Long.class || tipo == Integer.class || tipo == Short.class || tipo == Byte.class
                || tipo == Double.class || tipo == Float.class || tipo == Boolean.class || tipo == Character.class
                || tipo == BigDecimal.class
                || tipo == LocalDate.class || tipo == LocalDateTime.class || tipo == LocalTime.class
                || tipo.isEnum();
    }

    private String obtenerNombreTabla(Class<?> clase) {
        Table tabla = clase.getAnnotation(Table.class);
        return (tabla != null && !tabla.name().isBlank()) ? tabla.name() : clase.getSimpleName().toLowerCase();
    }

    private Long invocarGetterLong(Object entity, String nombreMetodo) {
        try {
            Method metodo = entity.getClass().getMethod(nombreMetodo);
            Object valor = metodo.invoke(entity);
            return valor instanceof Long ? (Long) valor : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "SISTEMA";
        }
        return auth.getName();
    }

    private String obtenerIpCliente() {
        HttpServletRequest request = obtenerRequestActual();
        return request != null ? request.getRemoteAddr() : null;
    }

    private String obtenerNavegador() {
        HttpServletRequest request = obtenerRequestActual();
        return request != null ? request.getHeader("User-Agent") : null;
    }

    private HttpServletRequest obtenerRequestActual() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attrs.getRequest();
        } catch (IllegalStateException e) {
            return null; // no hay request HTTP activo (ej. proceso batch/seed)
        }
    }

    private static final class PendienteAuditoria {
        final String tabla;
        final Long idRegistro;
        final String accion;
        final String datosAntes;
        final String datosDespues;
        final Long versionEntity;

        PendienteAuditoria(String tabla, Long idRegistro, String accion,
                           String datosAntes, String datosDespues, Long versionEntity) {
            this.tabla = tabla;
            this.idRegistro = idRegistro;
            this.accion = accion;
            this.datosAntes = datosAntes;
            this.datosDespues = datosDespues;
            this.versionEntity = versionEntity;
        }
    }
}