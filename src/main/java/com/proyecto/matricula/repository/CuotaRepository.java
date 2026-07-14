package com.proyecto.matricula.repository;

import com.proyecto.matricula.entity.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Integer> {

    List<Cuota> findByMatriculaCodMatriculaAndEstadoTrueOrderByOrdenPagoAsc(Integer codMatricula);

    // Obtener cuotas pendientes de un alumno filtradas por año académico
    @Query("SELECT c FROM Cuota c WHERE c.matricula.alumno.codAlumno = :codAlumno " +
            "AND c.matricula.anioAcademico.codAnioAcademico = :codAnioAcademico " +
            "AND c.estado = true AND c.pagado = false ORDER BY c.ordenPago ASC")
    List<Cuota> findPendingCuotas(@Param("codAlumno") Integer codAlumno,
                                  @Param("codAnioAcademico") Integer codAnioAcademico);

    // Obtener todas las deudas de un alumno (de cualquier año)
    @Query("SELECT c FROM Cuota c WHERE c.matricula.alumno.codAlumno = :codAlumno " +
            "AND c.estado = true AND c.pagado = false ORDER BY c.matricula.anioAcademico.anio ASC, c.ordenPago ASC")
    List<Cuota> findAllPendingCuotasByAlumno(@Param("codAlumno") Integer codAlumno);

    Optional<Cuota> findByCodCuotaAndEstadoTrue(Integer codCuota);
    @Query("SELECT COALESCE(SUM(c.montoPendiente), 0) FROM Cuota c " +
            "WHERE c.matricula.anioAcademico.anio = :anio " +
            "AND c.pagado = false AND c.estado = true")
    BigDecimal sumMontoPendienteByAnio(@Param("anio") Integer anio);

    @Query("SELECT COUNT(c) FROM Cuota c " +
            "WHERE c.matricula.anioAcademico.anio = :anio " +
            "AND c.pagado = false AND c.estado = true")
    long countPendientesByAnio(@Param("anio") Integer anio);
}
