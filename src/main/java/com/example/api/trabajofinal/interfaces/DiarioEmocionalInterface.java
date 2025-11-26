package com.example.api.trabajofinal.interfaces;

import com.example.api.trabajofinal.DTO.*;
import com.example.api.trabajofinal.entities.DiarioEmocional;
import java.util.List;

public interface DiarioEmocionalInterface {
    DiarioEmocional registrarEmocion(Long niñoId, String emocion, String contexto);
    List<DiarioEmocional> obtenerHistorialPorNiño(Long niñoId);
    List<DiarioEmocional> obtenerUltimasEmociones(Long niñoId, int limite);
    List<DiarioEmocional> obtenerEmocionesPorFecha(Long niñoId, String fecha);

    List<PreguntaEmocionalDTO> generarPreguntasDiarias(Long niñoId);
    RespuestaEmocionalDTO procesarRespuestaEmocional(RegistroEmocionalDTO registroDTO);
    RegistroEmocionalDTO obtenerRegistroDelDia(Long niñoId);
    Boolean sincronizarDatosOffline(Long niñoId);
    List<DiarioEmocionalDTO> findAllByNiñoId(Long niñoId);
}