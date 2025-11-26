package com.example.api.trabajofinal.security.service;

import com.example.api.trabajofinal.security.entities.PasswordResetToken;
import com.example.api.trabajofinal.entities.Usuario;
import com.example.api.trabajofinal.security.repositories.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class TokenService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetToken createPasswordResetToken(Usuario usuario, int expirationHours) {
        try {
            System.out.println("=== 🔑 TOKEN SERVICE - INICIANDO ===");
            System.out.println("Usuario ID: " + usuario.getId());
            System.out.println("Correo: " + usuario.getCorreoElectronico());

            passwordResetTokenRepository.deleteByUsuario(usuario);
            System.out.println("Eliminando Tokens antiguos");

            String token = UUID.randomUUID().toString();
            System.out.println("Nuevo token generado: " + token);

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUsuario(usuario);
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(expirationHours));

            PasswordResetToken savedToken = passwordResetTokenRepository.save(resetToken);
            System.out.println("TOKEN GUARDADO EN BD - ID: " + savedToken.getId());
            System.out.println("=== 🔑 TOKEN SERVICE - FINALIZADO ===");

            return savedToken;

        } catch (Exception e) {
            System.out.println("ERROR EN TOKEN SERVICE: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public PasswordResetToken findPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    public void deletePasswordResetToken(PasswordResetToken token) {
        passwordResetTokenRepository.delete(token);
    }
}