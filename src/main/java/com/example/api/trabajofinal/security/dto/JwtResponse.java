package com.example.api.trabajofinal.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String tipo = "Bearer";
    private String correoElectronico;
    private String tipoUsuario;
    private String nombreCompleto;
    private Long id;

    public JwtResponse(String token, String correoElectronico, String tipoUsuario, String nombreCompleto, Long id) {
        this.token = token;
        this.correoElectronico = correoElectronico;
        this.tipoUsuario = tipoUsuario;
        this.nombreCompleto = nombreCompleto;
        this.id = id;
    }
}