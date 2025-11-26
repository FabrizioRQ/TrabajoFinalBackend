package com.example.api.trabajofinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetodoPagoDTO {
    private Long id;
    private String tipo; // "tarjeta_credito", "billetera_digital", "transferencia"
    private String ultimosDigitos; // "****1234"
    private Boolean predeterminado;
    private String estado; // "ACTIVO", "INACTIVO"
    private Long usuarioId;
    private Instant fechaCreacion;
}