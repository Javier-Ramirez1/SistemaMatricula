package pe.edu.unjfsc.sistemamatricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import org.springframework.data.repository.query.Param;
import pe.edu.unjfsc.sistemamatricula.entity.academico.Aula;

import java.util.List;
import java.util.Optional;

public interface AulaRepository extends JpaRepository<Aula, Long> {

    List<Aula> findByAnioAcademicoIdAndEstado(Long idAnioAcademico, Byte estado);

    long countByAnioAcademicoIdAndEstado(Long idAnioAcademico, Byte estado);

    Optional<Aula> findByAnioAcademicoIdAndNivelIdAndGradoIdAndSeccionId(
            Long idAnioAcademico, Long idNivel, Long idGrado, Long idSeccion);

    /**
     * Bloqueo pesimista (SELECT ... FOR UPDATE) sobre el aula al
     * momento de matricular: evita que dos secretarias matriculen
     * al mismo tiempo en la última vacante disponible y ambas
     * pasen la validación de cupo con datos obsoletos.
     * Complementa (no reemplaza) el @Version de la entidad.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Aula a WHERE a.id = :id")
    Optional<Aula> findByIdForUpdate(@Param("id") Long id);
}