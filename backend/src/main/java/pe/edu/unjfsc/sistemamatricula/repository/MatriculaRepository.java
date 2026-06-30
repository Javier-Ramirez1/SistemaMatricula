package pe.edu.unjfsc.sistemamatricula.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Matricula;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    Optional<Matricula> findByAlumnoIdAndAnioAcademicoId(Long idAlumno, Long idAnioAcademico);

    boolean existsByAlumnoIdAndAnioAcademicoIdAndEstadoMatricula(
            Long idAlumno, Long idAnioAcademico, pe.edu.unjfsc.sistemamatricula.entity.enums.EstadoMatricula estadoMatricula);

    List<Matricula> findByAulaIdAndEstadoMatricula(Long idAula, pe.edu.unjfsc.sistemamatricula.entity.enums.EstadoMatricula estadoMatricula);

    List<Matricula> findByAnioAcademicoIdAndEstado(Long idAnioAcademico, Byte estado);

    /**
     * Conteo de matrículas activas agrupadas por nivel — alimenta el
     * gráfico de barras "Matrículas por Nivel" del dashboard del Director.
     * Devuelve Object[]{nombreNivel (String), cantidad (Long)}.
     */
    @org.springframework.data.jpa.repository.Query(
            "SELECT m.aula.nivel.nombre, COUNT(m) FROM Matricula m " +
                    "WHERE m.anioAcademico.id = :idAnioAcademico AND m.estado = 1 " +
                    "AND m.estadoMatricula = pe.edu.unjfsc.sistemamatricula.entity.enums.EstadoMatricula.ACTIVA " +
                    "GROUP BY m.aula.nivel.nombre")
    List<Object[]> contarMatriculasPorNivel(@org.springframework.data.repository.query.Param("idAnioAcademico") Long idAnioAcademico);

    long countByAnioAcademicoIdAndEstadoAndEstadoMatricula(
            Long idAnioAcademico, Byte estado, pe.edu.unjfsc.sistemamatricula.entity.enums.EstadoMatricula estadoMatricula);
}