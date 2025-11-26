package com.example.api.trabajofinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PsicologoDTO {
    private Long id;
    private String especialidad;
    private String numeroColegiatura;
    private Long idUsuario;
}
