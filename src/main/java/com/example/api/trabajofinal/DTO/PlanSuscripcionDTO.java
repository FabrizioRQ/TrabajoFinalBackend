package com.example.api.trabajofinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanSuscripcionDTO {
    private String codigo;
    private String nombre;
    private Double precioMensual;
    private String descripcion;
    private String caracteristicas;
    private Boolean activo;
}