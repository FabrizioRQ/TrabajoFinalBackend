package com.example.api.trabajofinal.repositories;

import com.example.api.trabajofinal.entities.Niño;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface NiñoRepository extends JpaRepository<Niño, Long> {
    @Query("SELECT COUNT(n) > 0 FROM Niño n WHERE n.idUsuario.id = :idUsuario")
    boolean existsByIdUsuario(@Param("idUsuario") Long idUsuario);

    @Query("SELECT COUNT(n) FROM Niño n WHERE n.idPsicologo.id = :idPsicologo")
    long countByIdPsicologo(@Param("idPsicologo") Long idPsicologo);

    @Query("SELECT n FROM Niño n WHERE n.idUsuario.id = :usuarioId")
    Optional<Niño> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Alternativa 2: Usando @Query
    @Query("SELECT n FROM Niño n WHERE n.idPsicologo.id = :idPsicologo")
    List<Niño> findByPsicologoId(@Param("idPsicologo") Long idPsicologo);


    // CONSULTA COMPLEJA 1: Niños con emociones registradas en un rango de fechas
    @Query("SELECT DISTINCT n FROM Niño n JOIN n.diariosEmocionales d " +
            "WHERE d.fecha BETWEEN :fechaInicio AND :fechaFin " +
            "ORDER BY n.fechaNacimiento")
    List<Niño> findNiñosConEmocionesEnRangoFechas(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    // CONSULTA COMPLEJA 2: Niños por psicólogo con conteo de registros emocionales
    @Query("SELECT n, COUNT(d) as totalRegistros " +
            "FROM Niño n LEFT JOIN n.diariosEmocionales d " +
            "WHERE n.idPsicologo.id = :idPsicologo " +
            "GROUP BY n " +
            "ORDER BY totalRegistros DESC")
    List<Object[]> findNiñosConConteoRegistrosByPsicologo(@Param("idPsicologo") Long idPsicologo);

    // REPORTE COMPLEJO 1: Estadísticas de emociones por niño en un período
    @Query("SELECT n.id, n.fechaNacimiento, u.nombreCompleto, d.emocionRegistrada, COUNT(d) as frecuencia " +
            "FROM Niño n JOIN n.idUsuario u LEFT JOIN n.diariosEmocionales d " +
            "WHERE d.fecha BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY n.id, n.fechaNacimiento, u.nombreCompleto, d.emocionRegistrada " +
            "ORDER BY n.id, frecuencia DESC")
    List<Object[]> findEstadisticasEmocionesPorNiño(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);


    @Query("SELECT n.id, u.nombreCompleto as nombreNiño, n.fechaNacimiento, " +
            "a.nombreAvatar, psicoUsuario.nombreCompleto as nombrePsicologo, " +
            "n.idPadre.id as idPadre, " +  // Solo el ID del padre
            "COUNT(d) as totalRegistros, " +
            "MAX(d.fecha) as ultimoRegistro " +
            "FROM Niño n " +
            "JOIN n.idUsuario u " +
            "JOIN n.idAvatar a " +
            "JOIN n.idPsicologo p " +
            "JOIN p.idUsuario psicoUsuario " +
            "LEFT JOIN n.diariosEmocionales d " +
            "WHERE n.idPsicologo.id = :idPsicologo " +
            "GROUP BY n.id, u.nombreCompleto, n.fechaNacimiento, a.nombreAvatar, " +
            "psicoUsuario.nombreCompleto, n.idPadre.id " +
            "ORDER BY ultimoRegistro DESC NULLS LAST")
    List<Object[]> findDashboardNiñosPorPsicologo(@Param("idPsicologo") Long idPsicologo);
}