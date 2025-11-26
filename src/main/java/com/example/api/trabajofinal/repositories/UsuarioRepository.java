package com.example.api.trabajofinal.repositories;

import com.example.api.trabajofinal.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    public Usuario findByCorreoElectronico(String correo);
    public Optional<Usuario> findById(Long id);
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r.name = :rol")
    List<Usuario> findUsuariosByRol(@Param("rol") String rol);

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombreCompleto) LIKE LOWER(CONCAT(:inicio, '%'))")
    List<Usuario> findByNombreStartsWith(@Param("inicio") String inicio);

    @Query("SELECT p.idUsuario FROM Psicologo p")
    List<Usuario> findUsuariosQueSonPsicologos();

    @Query("""
    SELECT DISTINCT u FROM Usuario u
    LEFT JOIN u.roles r
    WHERE (:correo IS NULL 
           OR LOWER(u.correoElectronico) LIKE LOWER(CONCAT('%', :correo, '%')))
      AND (:nombre IS NULL 
           OR LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :nombre, '%')))
      AND (:tipo IS NULL 
           OR LOWER(u.tipoUsuario) LIKE LOWER(CONCAT('%', :tipo, '%')))
      AND (:estado IS NULL 
           OR LOWER(u.estado) LIKE LOWER(CONCAT('%', :estado, '%')))
      AND (:rol IS NULL 
           OR EXISTS (
               SELECT 1 FROM u.roles r2 
               WHERE LOWER(r2.name) LIKE LOWER(CONCAT('%', :rol, '%'))
           ))
""")
    List<Usuario> busquedaAvanzada(
            @Param("correo") String correo,
            @Param("nombre") String nombre,
            @Param("tipo") String tipo,
            @Param("estado") String estado,
            @Param("rol") String rol
    );
}
