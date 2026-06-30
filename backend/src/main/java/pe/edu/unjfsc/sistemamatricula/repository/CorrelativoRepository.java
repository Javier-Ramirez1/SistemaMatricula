package pe.edu.unjfsc.sistemamatricula.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Correlativo;

import java.util.Optional;

public interface CorrelativoRepository extends JpaRepository<Correlativo, Long> {

    Optional<Correlativo> findByAnioAcademicoId(Long idAnioAcademico);

    /**
     * SELECT ... FOR UPDATE sobre el correlativo del año: bloqueo
     * pesimista a nivel de fila, imprescindible para que dos pagos
     * concurrentes NUNCA generen el mismo número de boleta.
     * El optimistic lock (@Version) no es suficiente aquí porque
     * incrementar un contador es exactamente el caso de alta
     * concurrencia que el bloqueo pesimista está hecho para resolver.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Correlativo c WHERE c.anioAcademico.id = :idAnioAcademico")
    Optional<Correlativo> findByAnioAcademicoIdForUpdate(@Param("idAnioAcademico") Long idAnioAcademico);
}