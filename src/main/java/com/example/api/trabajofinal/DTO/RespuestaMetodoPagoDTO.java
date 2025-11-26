package com.example.api.trabajofinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaMetodoPagoDTO {
    private Boolean exito;
    private String mensaje;
    private MetodoPagoDTO metodoPago;
}