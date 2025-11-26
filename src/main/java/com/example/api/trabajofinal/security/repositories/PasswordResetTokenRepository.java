package com.example.api.trabajofinal.security.repositories;

import com.example.api.trabajofinal.security.entities.PasswordResetToken;
import com.example.api.trabajofinal.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);

    @Modifying
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.usuario = :usuario")
    void deleteByUsuario(@Param("usuario") Usuario usuario);
}