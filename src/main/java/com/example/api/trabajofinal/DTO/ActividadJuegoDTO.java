package com.example.api.trabajofinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActividadJuegoDTO {
    private Long id;
    private String nombre;
    private String emocion;
    private Integer puntosGanados;
    private Long idNiño;
}
