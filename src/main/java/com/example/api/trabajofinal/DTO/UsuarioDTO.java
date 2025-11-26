package com.example.api.trabajofinal.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String correoElectronico;
    @JsonIgnore
    private String contraseña;
    private String tipoUsuario;
    private String nombreCompleto;
}