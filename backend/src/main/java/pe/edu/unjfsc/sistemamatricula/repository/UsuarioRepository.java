package pe.edu.unjfsc.sistemamatricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.unjfsc.sistemamatricula.entity.seguridad.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByUsernameAndEstado(String username, Byte estado);
    boolean existsByUsername(String username);
    boolean existsByTipoDocumentoIdAndNumeroDocumento(Long idTipoDocumento, String numeroDocumento);
    List<Usuario> findByEstado(Byte estado);
}