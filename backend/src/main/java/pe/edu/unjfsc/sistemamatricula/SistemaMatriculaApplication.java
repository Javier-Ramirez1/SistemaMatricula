package pe.edu.unjfsc.sistemamatricula;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Punto de entrada de la aplicación.
 *
 * @EnableTransactionManagement: habilita @Transactional en los Process
 * (MatriculaProcess, PagoProcess) — necesario para que Spring intercepte
 * los métodos y maneje commit/rollback automáticamente.
 *
 * @EnableAsync: opcional, útil si luego se generan reportes pesados
 * (JasperReports) en segundo plano sin bloquear al usuario.
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class SistemaMatriculaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaMatriculaApplication.class, args);
    }
}
