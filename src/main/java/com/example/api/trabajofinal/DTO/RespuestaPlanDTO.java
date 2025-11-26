package com.example.api.trabajofinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaPlanDTO {
    private Boolean exito;
    private String mensaje;
    private String planSeleccionado;
    private String redireccion;
}