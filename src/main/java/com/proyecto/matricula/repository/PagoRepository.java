package com.proyecto.matricula.repository;

import com.proyecto.matricula.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    List<Pago> findByCuotaCodCuotaAndEstadoTrue(Integer codCuota);
    List<Pago> findAllByCuotaMatriculaAlumnoCodAlumnoAndEstadoTrueOrderByFechaPagoDesc(Integer codAlumno);

    @Query("SELECT MONTH(p.fechaPago) as mes, SUM(p.montoPagado) as total " +
           "FROM Pago p " +
           "WHERE YEAR(p.fechaPago) = :anio AND p.estado = true " +
           "GROUP BY MONTH(p.fechaPago) " +
           "ORDER BY MONTH(p.fechaPago)")
    List<Object[]> sumMontoPagadoByMonth(@Param("anio") Integer anio);

    @Query("SELECT SUM(p.montoPagado) FROM Pago p " +
           "WHERE MONTH(p.fechaPago) = :mes AND YEAR(p.fechaPago) = :anio AND p.estado = true")
    BigDecimal sumMontoPagadoByMonthAndYear(@Param("mes") Integer mes, @Param("anio") Integer anio);
    @Query("SELECT COALESCE(SUM(p.montoPagado), 0) FROM Pago p " +
            "WHERE p.cuota.concepto.tipoConcepto.descripcion = :tipo " +
            "AND YEAR(p.fechaPago) = :anio AND p.estado = true")
    BigDecimal sumMontoPagadoByTipoConceptoAndAnio(@Param("tipo") String tipo, @Param("anio") Integer anio);

    @Query("SELECT COUNT(p) FROM Pago p " +
            "WHERE p.cuota.concepto.tipoConcepto.descripcion = :tipo " +
            "AND YEAR(p.fechaPago) = :anio AND p.estado = true")
    long countByTipoConceptoAndAnio(@Param("tipo") String tipo, @Param("anio") Integer anio);
}
