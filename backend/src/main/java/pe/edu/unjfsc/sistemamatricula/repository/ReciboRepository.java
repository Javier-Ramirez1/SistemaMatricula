package pe.edu.unjfsc.sistemamatricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Recibo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReciboRepository extends JpaRepository<Recibo, Long> {
    Optional<Recibo> findByNumeroBoleta(String numeroBoleta);
    Optional<Recibo> findByPagoId(Long idPago);
    List<Recibo> findByMatriculaIdOrderByFechaEmisionDesc(Long idMatricula);

    @Query("SELECT COALESCE(SUM(r.montoPagado), 0) FROM Recibo r " +
            "WHERE r.fechaEmision >= :desde AND r.fechaEmision < :hasta AND r.estado = 1")
    BigDecimal sumarMontoEntreFechas(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    /**
     * Ingresos agrupados por mes (1-12) del año dado — alimenta el
     * gráfico "Resumen de Ingresos" del dashboard del Director.
     * Devuelve Object[]{mes (Integer), total (BigDecimal)}.
     */
    @Query("SELECT EXTRACT(MONTH FROM r.fechaEmision), COALESCE(SUM(r.montoPagado), 0) " +
            "FROM Recibo r WHERE EXTRACT(YEAR FROM r.fechaEmision) = :anio AND r.estado = 1 " +
            "GROUP BY EXTRACT(MONTH FROM r.fechaEmision) " +
            "ORDER BY EXTRACT(MONTH FROM r.fechaEmision)")
    List<Object[]> sumarMontoPorMes(@Param("anio") int anio);

    List<Recibo> findByFechaEmisionBetweenOrderByFechaEmisionDesc(LocalDateTime desde, LocalDateTime hasta);
}