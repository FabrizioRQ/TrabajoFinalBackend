package com.example.api.trabajofinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeleccionPlanDTO {
    private String codigoPlan;
    private Long usuarioId;
    private String metodoPago;
}