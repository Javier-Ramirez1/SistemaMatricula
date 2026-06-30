package pe.edu.unjfsc.sistemamatricula.auditoria;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * Puente entre Spring y AuditoriaListener. AuditoriaListener es un
 * EntityListener JPA (lo instancia Hibernate, no Spring), así que no
 * puede recibir @Autowired — esta clase, que SÍ es un bean de Spring,
 * le pasa la referencia de AuditoriaService una sola vez al arrancar.
 */
@Configuration
@RequiredArgsConstructor
public class AuditoriaConfig {

    private final AuditoriaService auditoriaService;

    @PostConstruct
    public void inicializarPuenteDeAuditoria() {
        AuditoriaListener.inicializar(auditoriaService);
    }
}