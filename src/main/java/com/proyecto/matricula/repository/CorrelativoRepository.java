package com.proyecto.matricula.repository;

import com.proyecto.matricula.entity.Correlativo;
import com.proyecto.matricula.entity.CorrelativoId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorrelativoRepository extends JpaRepository<Correlativo, CorrelativoId> {
    
    // Bloqueo pesimista de escritura para garantizar unicidad correlativa en transacciones concurrentes
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Correlativo c WHERE c.tipoComprobante = :tipo AND c.serie = :serie AND c.estado = true")
    Optional<Correlativo> findAndLockByTipoAndSerie(@Param("tipo") String tipo, @Param("serie") String serie);
}
