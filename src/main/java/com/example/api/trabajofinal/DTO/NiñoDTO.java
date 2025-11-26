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
public class NiñoDTO {
    private Long id;
    private LocalDate fechaNacimiento;
    private Long idUsuario;
    private Long idAvatar;
    private Long idPsicologo;
    private Long idPadre;
}
