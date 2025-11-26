package com.example.api.trabajofinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CancelacionSuscripcionDTO {
    private Long usuarioId;
    private String motivo;
    private Boolean pausarPagos;
}