package com.example.api.trabajofinal.repositories;

import com.example.api.trabajofinal.entities.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago,Long> {
    public List<Pago> findByIdUsuario_Id(Long usuarioId);

    @Query("SELECT u.nombreCompleto, SUM(p.monto) " +
            "FROM Pago p JOIN p.idUsuario u " +
            "WHERE p.fechaPago BETWEEN :inicio AND :fin " +
            "GROUP BY u.nombreCompleto")
    List<Object[]> totalPagadoPorUsuarioEnRango(Instant inicio, Instant fin);

    @Query("SELECT p.estado, p.metodoPago, COUNT(p) " +
            "FROM Pago p " +
            "GROUP BY p.estado, p.metodoPago")
    List<Object[]> countPagosPorEstadoYMetodo();

    @Query(value = "SELECT EXTRACT(MONTH FROM fecha_pago), SUM(monto) " +
            "FROM pago WHERE fecha_pago >= :fechaInicio " +
            "GROUP BY EXTRACT(MONTH FROM fecha_pago) " +
            "ORDER BY EXTRACT(MONTH FROM fecha_pago)", nativeQuery = true)
    List<Object[]> ingresosMensuales(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT u.nombreCompleto, COUNT(p), SUM(p.monto) " +
            "FROM Pago p JOIN p.idUsuario u " +
            "GROUP BY u.nombreCompleto " +
            "ORDER BY SUM(p.monto) DESC")
    List<Object[]> topUsuariosConMasPagos();

}
