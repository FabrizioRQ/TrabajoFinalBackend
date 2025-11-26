package com.example.api.trabajofinal.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String correoElectronico;
    private String contraseña;
}
