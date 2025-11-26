package com.example.api.trabajofinal.repositories;

import com.example.api.trabajofinal.entities.DiarioEmocional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiarioEmocionalRepository extends JpaRepository<DiarioEmocional, Long> {

    List<DiarioEmocional> findByIdNiñoIdOrderByFechaDesc(Long idNiño);

    List<DiarioEmocional> findByIdNiñoIdAndFecha(Long idNiño, LocalDate fecha);

    @Query("SELECT d FROM DiarioEmocional d WHERE d.idNiño.id = :niñoId AND d.fecha BETWEEN :startDate AND :endDate ORDER BY d.fecha DESC")
    List<DiarioEmocional> findByIdNiñoIdAndFechaBetweenOrderByFechaDesc(
            @Param("niñoId") Long niñoId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    // Opción 1: Usando el nombre correcto de la propiedad
    @Query("SELECT d FROM DiarioEmocional d WHERE d.idNiño.id = :niñoId")
    List<DiarioEmocional> findAllByNiñoId(@Param("niñoId") Long niñoId);


}