package com.example.api.trabajofinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaEmocionalDTO {
    private Boolean exito;
    private String mensaje;
    private String emocionDetectada;
    private String recomendacion;
    private Boolean esCritico;
    private LocalDateTime timestamp;
}