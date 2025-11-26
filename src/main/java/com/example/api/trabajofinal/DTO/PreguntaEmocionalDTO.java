package com.example.api.trabajofinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreguntaEmocionalDTO {
    private String pregunta;
    private List<String> opciones;
    private String tipo;
    private String contexto;
}