package com.example.api.trabajofinal.repositories;

import com.example.api.trabajofinal.entities.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PsicologoRepository extends JpaRepository<Psicologo, Long> {
    public boolean existsByNumeroColegiatura(String numeroColegiatura);

    @Query("SELECT COUNT(p) > 0 FROM Psicologo p WHERE p.idUsuario.id = :idUsuario")
    boolean existsByIdUsuario(@Param("idUsuario") Long idUsuario);

    List<Psicologo> findByEspecialidad(String especialidad);
    Psicologo findByNumeroColegiatura(String numeroColegiatura);

    @Query("""
           SELECT p 
           FROM Psicologo p 
           JOIN FETCH p.idUsuario u
           """)
    List<Psicologo> findAllWithUsuario();

    @Query("""
           SELECT p
           FROM Psicologo p
           JOIN p.idUsuario u
           WHERE LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :nombre, '%'))
           AND (:especialidad IS NULL OR LOWER(p.especialidad) = LOWER(:especialidad))
           """)
    List<Psicologo> buscarPorNombreYEspecialidad(
            @Param("nombre") String nombre,
            @Param("especialidad") String especialidad
    );

}