package com.example.api.trabajofinal.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroRequestDTO {
    private String correoElectronico;
    private String contraseña;
    private String tipoUsuario;
    private String nombreCompleto;
}