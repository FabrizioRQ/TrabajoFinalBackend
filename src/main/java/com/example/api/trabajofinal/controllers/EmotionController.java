package com.example.api.trabajofinal.controllers;

import com.example.api.trabajofinal.services.EmotionAIService;
import com.example.api.trabajofinal.services.DiarioEmocionalService;
import com.example.api.trabajofinal.services.NiñoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/terapia")
@CrossOrigin("*")
public class EmotionController {

    @Autowired
    private EmotionAIService emotionAIService;

    @Autowired
    private DiarioEmocionalService diarioEmocionalService;

    @Autowired
    private NiñoService niñoService;

    @PostMapping("/analizar-emocion")
    public ResponseEntity<?> analizarEmocion(@RequestBody Map<String, String> request) {
        try {
            String texto = request.get("texto");
            Long niñoId = Long.valueOf(request.get("niñoId"));

            if (texto == null || texto.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Texto requerido para análisis");
            }

            if (niñoService.obtenerNiñoPorId(niñoId).isEmpty()) {
                return ResponseEntity.badRequest().body("Niño no encontrado");
            }

            EmotionAIService.AnalisisEmocional analisis = emotionAIService.analizarTexto(texto);

            if (!analisis.getEmocionDetectada().equals("NEUTRAL") && !analisis.isCritico()) {
                diarioEmocionalService.registrarEmocion(niñoId, analisis.getEmocionDetectada(), texto);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("emocionDetectada", analisis.getEmocionDetectada());
            response.put("confianza", analisis.getConfianza());
            response.put("recomendacion", analisis.getRecomendacion());
            response.put("critico", analisis.isCritico());
            response.put("timestamp", analisis.getTimestamp());

            if (!analisis.getEmocionDetectada().equals("NEUTRAL")) {
                response.put("mensaje", generarMensajeIntervencion(analisis.getEmocionDetectada()));
            }

            System.out.println("Análisis emocional completado: " + analisis.getEmocionDetectada());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error en análisis emocional: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error en análisis emocional: " + e.getMessage());
        }
    }

    @GetMapping("/historial-emocional/{niñoId}")
    public ResponseEntity<?> obtenerHistorialEmocional(@PathVariable Long niñoId) {
        try {
            if (niñoService.obtenerNiñoPorId(niñoId).isEmpty()) {
                return ResponseEntity.badRequest().body("Niño no encontrado");
            }

            var historial = diarioEmocionalService.obtenerHistorialPorNiño(niñoId);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            System.out.println("Error obteniendo historial: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error obteniendo historial: " + e.getMessage());
        }
    }

    @GetMapping("/ultimas-emociones/{niñoId}")
    public ResponseEntity<?> obtenerUltimasEmociones(@PathVariable Long niñoId,
                                                     @RequestParam(defaultValue = "5") int limite) {
        try {
            if (niñoService.obtenerNiñoPorId(niñoId).isEmpty()) {
                return ResponseEntity.badRequest().body("Niño no encontrado");
            }

            var emociones = diarioEmocionalService.obtenerUltimasEmociones(niñoId, limite);
            return ResponseEntity.ok(emociones);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error obteniendo emociones: " + e.getMessage());
        }
    }

    private String generarMensajeIntervencion(String emocion) {
        switch (emocion) {
            case "ESTRES":
                return "Parece que estás sintiendo algo de estrés. ¿Te gustaría probar un ejercicio de respiración?";
            case "ANSIEDAD":
                return "Noto que podrías estar sintiendo ansiedad. ¿Quieres intentar una técnica de grounding?";
            case "TRISTEZA":
                return "Parece que estás pasando por un momento difícil. ¿Te gustaría hablar sobre ello?";
            case "ENOJO":
                return "Veo que algo te ha molestado. ¿Quieres explorar formas de manejar esta emoción?";
            case "MIEDO":
                return "Parece que algo te está causando temor. ¿Te gustaría trabajar en ello juntos?";
            default:
                return "He notado algunos cambios en tu estado emocional. ¿Quieres explorar algunas técnicas de bienestar?";
        }
    }
}