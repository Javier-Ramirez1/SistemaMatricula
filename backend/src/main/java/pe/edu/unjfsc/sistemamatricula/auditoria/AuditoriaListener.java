package pe.edu.unjfsc.sistemamatricula.auditoria;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreRemove;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * EntityListener genérico, enganchado en Auditable (@MappedSuperclass)
 * para auditar automáticamente CUALQUIER entidad de negocio (alumno,
 * concepto, matrícula, cuota, pago, recibo, usuario, rol, etc.) sin
 * repetir la anotación en cada clase hija — ver entity/Auditable.java.
 *
 * No es un bean de Spring: lo instancia el proveedor JPA (Hibernate),
 * así que no se le puede hacer @Autowired directamente. Recibe la
 * referencia a AuditoriaService a través de un puente estático que
 * AuditoriaConfig inicializa una sola vez al arrancar el contexto.
 */
public class AuditoriaListener {

    private static volatile AuditoriaService auditoriaService;

    /** Invocado por AuditoriaConfig al iniciar la aplicación. */
    public static void inicializar(AuditoriaService service) {
        auditoriaService = service;
    }

    /**
     * Snapshot "antes": se captura en el momento en que Hibernate trae
     * la entidad desde la base de datos, que es el único punto donde
     * un EntityListener JPA puede conocer el estado previo real de la
     * fila (en @PostUpdate la entidad en memoria ya tiene los valores
     * NUEVOS). Se guarda en un mapa por identidad de objeto y vive
     * mientras la entidad esté en memoria (WeakHashMap evita fugas).
     */
    private static final Map<Object, String> snapshots = Collections.synchronizedMap(new WeakHashMap<>());

    @PostLoad
    public void alCargar(Object entity) {
        if (auditoriaService == null) return;
        snapshots.put(entity, auditoriaService.serializar(entity));
    }

    @PostPersist
    public void alInsertar(Object entity) {
        if (auditoriaService == null) return;
        String despues = auditoriaService.serializar(entity);
        auditoriaService.encolar(entity, "INSERT", null, despues);
        snapshots.put(entity, despues);
    }

    @PostUpdate
    public void alActualizar(Object entity) {
        if (auditoriaService == null) return;
        String antes = snapshots.get(entity);
        String despues = auditoriaService.serializar(entity);
        auditoriaService.encolar(entity, "UPDATE", antes, despues);
        snapshots.put(entity, despues);
    }

    /**
     * Cubre el caso de un DELETE físico (entityManager.remove()). El
     * sistema usa borrado lógico (estado=0 + save) en casi todos los
     * services, lo cual ya queda cubierto por alActualizar(); este
     * callback es la red de seguridad por si alguna vez se elimina
     * un registro físicamente.
     */
    @PreRemove
    public void alEliminar(Object entity) {
        if (auditoriaService == null) return;
        auditoriaService.encolar(entity, "DELETE", auditoriaService.serializar(entity), null);
    }
}