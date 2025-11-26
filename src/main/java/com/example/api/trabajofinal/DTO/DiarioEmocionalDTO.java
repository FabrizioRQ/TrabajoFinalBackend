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
public class DiarioEmocionalDTO {
    private Long id;
    private LocalDate fecha;
    private String emocionRegistrada;
    private Long niñoId;
}