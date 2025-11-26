package com.example.api.trabajofinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistroEmocionalDTO {
    private Long niñoId;
    private String emocion;
    private String contexto;
    private Integer escalaEmocional; // 1-5
    private String respuestaTexto;
    private LocalDate fecha;
}