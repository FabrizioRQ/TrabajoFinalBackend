package com.example.api.trabajofinal.DTO;

import java.time.Instant;


public record PagoCreateDTO(
        Integer monto,
        Instant fechaPago,
        String metodoPago,
        String estado,
        Long usuarioId
) {}