package pe.edu.unjfsc.sistemamatricula.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * Configuración web general: CORS y recursos estáticos.
 *
 * CORS es necesario porque en la LAN el cliente (PC4) y el servidor
 * (PC1/PC3) corren en máquinas distintas — el navegador bloquearía
 * las peticiones cross-origin sin esta configuración.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // En producción reemplazar "*" por las IPs reales de la LAN,
                // ej: "http://192.168.1.10:8080", "http://192.168.1.11:8080"
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cachea recursos estáticos (jstree, datatables, sweetalert) por 1 día
        registry.addResourceHandler("/css/**", "/js/**", "/img/**", "/icons/**",
                        "/plugins/**", "/vendor/**")
                .addResourceLocations("classpath:/static/css/", "classpath:/static/js/",
                        "classpath:/static/img/", "classpath:/static/icons/",
                        "classpath:/static/plugins/", "classpath:/static/vendor/")
                .setCachePeriod((int) TimeUnit.DAYS.toSeconds(1));
    }
}