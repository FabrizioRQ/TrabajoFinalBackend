package com.example.api.trabajofinal.repositories;

import com.example.api.trabajofinal.entities.Padre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PadreRepository extends JpaRepository<Padre, Long> {
    // 1. Buscar por nombre exacto
    List<Padre> findByNombre(String nombre);

    // 2. Buscar por apellido que empiece con...
    List<Padre> findByApellidoStartingWith(String prefijo);

    // 🔹 CONSULTAS COMPLEJAS / REPORTES
    // 3. Contar niños asociados a cada padre (reporte)
    @Query("""
       SELECT p.nombre, p.apellido, COUNT(n.id)
       FROM Padre p
       LEFT JOIN p.niños n
       GROUP BY p.nombre, p.apellido
       """)
    List<Object[]> reportePadresConCantidadNiños();

    // 4. Listar padres que tienen niños menores de 10 años (reporte)
    @Query("""
       SELECT p FROM Padre p 
       JOIN p.niños n 
       WHERE n.fechaNacimiento > CURRENT_DATE - 18 YEAR
    """)
    List<Padre> padresConNiñosMenores();
}