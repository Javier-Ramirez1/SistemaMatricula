package com.proyecto.matricula.repository;

import com.proyecto.matricula.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsernameAndEstadoTrue(String username);
    Optional<Usuario> findByIdUsuarioAndEstadoTrue(Integer idUsuario);
}
