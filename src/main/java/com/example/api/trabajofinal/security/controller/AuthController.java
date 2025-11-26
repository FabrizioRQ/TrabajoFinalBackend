package com.example.api.trabajofinal.security.controller;

import com.example.api.trabajofinal.security.dto.JwtResponse;
import com.example.api.trabajofinal.DTO.LoginRequest;
import com.example.api.trabajofinal.DTO.RegistroRequestDTO;
import com.example.api.trabajofinal.DTO.UsuarioDTO;
import com.example.api.trabajofinal.security.service.CustomUserDetailsService;
import com.example.api.trabajofinal.security.util.JwtUtil;
import com.example.api.trabajofinal.security.service.RateLimiterService;
import com.example.api.trabajofinal.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RateLimiterService rateLimiterService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            System.out.println("INTENTO DE LOGIN: " + loginRequest.getCorreoElectronico());

            String key = request.getRemoteAddr() + ":" + loginRequest.getCorreoElectronico();
            if (rateLimiterService.isBlocked(key)) {
                System.out.println("LOGIN BLOQUEADO POR RATE LIMITING: " + loginRequest.getCorreoElectronico());
                return ResponseEntity.badRequest().body("Demasiados intentos, intente de nuevo más tarde");
            }

            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getCorreoElectronico(),
                                loginRequest.getContraseña()
                        )
                );
                rateLimiterService.resetAttempts(key);
                System.out.println("AUTENTICACIÓN EXITOSA: " + loginRequest.getCorreoElectronico());
            } catch (DisabledException de) {
                System.out.println("CUENTA DESHABILITADA: " + loginRequest.getCorreoElectronico());
                return ResponseEntity.badRequest().body("Credenciales no válidas, ingrese de nuevo sus datos");
            } catch (Exception e) {
                rateLimiterService.recordFailedAttempt(key);
                if (rateLimiterService.isBlocked(key)) {
                    System.out.println("CUENTA BLOQUEADA TEMPORALMENTE: " + loginRequest.getCorreoElectronico());
                    return ResponseEntity.badRequest().body("Demasiados intentos, intenta de nuevo en 15 minutos");
                }
                System.out.println("CREDENCIALES INVÁLIDAS: " + loginRequest.getCorreoElectronico());
                return ResponseEntity.badRequest().body("Credenciales no válidas, ingrese de nuevo sus datos");
            }

            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getCorreoElectronico());
            final String jwt = jwtUtil.generateToken(userDetails);
            UsuarioDTO usuario = usuarioService.obtenerPorCorreo(loginRequest.getCorreoElectronico());

            System.out.println("LOGIN EXITOSO - TOKEN GENERADO PARA: " + loginRequest.getCorreoElectronico());

            // 👇 MODIFICAR ESTA LÍNEA - Incluir nombreCompleto e ID
            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    usuario.getCorreoElectronico(),
                    usuario.getTipoUsuario(),
                    usuario.getNombreCompleto(),
                    usuario.getId()  // Asegúrate que UsuarioDTO tenga getId()
            ));

        } catch (Exception e) {
            System.out.println("ERROR EN LOGIN: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error interno del servidor, intento ingrese mas tarde");
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequestDTO registroRequest) {
        try {
            System.out.println("SOLICITUD REGISTRO: " + registroRequest.getCorreoElectronico());

            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setCorreoElectronico(registroRequest.getCorreoElectronico());
            usuarioDTO.setTipoUsuario("USER");
            usuarioDTO.setContraseña(registroRequest.getContraseña());
            usuarioDTO.setNombreCompleto(registroRequest.getNombreCompleto());

            UsuarioDTO nuevoUsuario = usuarioService.registrarConContraseña(usuarioDTO, registroRequest.getContraseña());

            System.out.println("USUARIO REGISTRADO COMO USER: " + registroRequest.getCorreoElectronico());
            return ResponseEntity.ok(nuevoUsuario);

        } catch (Exception e) {
            System.out.println("ERROR EN REGISTRO: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error en el registro: " + e.getMessage());
        }
    }
}