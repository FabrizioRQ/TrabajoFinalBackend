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
public class EstadoSuscripcionDTO {
    private Boolean activa;
    private String planActual;
    private Instant fechaProximoPago;
    private Instant fechaCancelacion;
    private Boolean pausada;
    private String motivoCancelacion;
}