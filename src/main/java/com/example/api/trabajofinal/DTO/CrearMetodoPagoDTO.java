package com.example.api.trabajofinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CrearMetodoPagoDTO {
    private String tipo;
    private String tokenProveedor;
    private Long usuarioId;
    private Boolean predeterminado;
}