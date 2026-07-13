package com.proyecto.matricula;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MatriculaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MatriculaApplication.class, args);
    }
}
