package com.example.api.trabajofinal.controllers;

import com.example.api.trabajofinal.DTO.*;
import com.example.api.trabajofinal.entities.DiarioEmocional;
import com.example.api.trabajofinal.services.DiarioEmocionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diario-emocional")
public class DiarioEmocionalController {

    @Autowired
    private DiarioEmocionalService diarioEmocionalService;

    //sera trabajado este
    @GetMapping("/preguntas-diarias/{niñoId}")
    public ResponseEntity<List<PreguntaEmocionalDTO>> generarPreguntasDiarias(@PathVariable Long niñoId) {
        try {
            List<PreguntaEmocionalDTO> preguntas = diarioEmocionalService.generarPreguntasDiarias(niñoId);
            return ResponseEntity.ok(preguntas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }



    @PostMapping("/procesar-respuesta")
    public ResponseEntity<RespuestaEmocionalDTO> procesarRespuesta(@RequestBody RegistroEmocionalDTO registroDTO) {
        try {
            RespuestaEmocionalDTO respuesta = diarioEmocionalService.procesarRespuestaEmocional(registroDTO);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new RespuestaEmocionalDTO(false, "Error: " + e.getMessage(), null, null, false, null)
            );
        }
    }

    @GetMapping("/registro-hoy/{niñoId}")
    public ResponseEntity<RegistroEmocionalDTO> obtenerRegistroHoy(@PathVariable Long niñoId) {
        try {
            RegistroEmocionalDTO registro = diarioEmocionalService.obtenerRegistroDelDia(niñoId);
            return ResponseEntity.ok(registro);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/sincronizar-offline/{niñoId}")
    public ResponseEntity<Boolean> sincronizarOffline(@PathVariable Long niñoId) {
        try {
            Boolean resultado = diarioEmocionalService.sincronizarDatosOffline(niñoId);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(false);
        }
    }

    @GetMapping("/niño/{niñoId}/entradas")
    public ResponseEntity<?> obtenerEntradasPorRangoFechas(
            @PathVariable Long niñoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            List<DiarioEmocionalDTO> entradas = diarioEmocionalService
                    .obtenerEntradasPorRangoFechas(niñoId, startDate, endDate);

            if (entradas.isEmpty()) {
                return ResponseEntity.ok()
                        .body(Map.of(
                                "message", "No se encontraron entradas en el rango de fechas especificado",
                                "entradas", List.of(),
                                "startDate", startDate,
                                "endDate", endDate
                        ));
            }

            return ResponseEntity.ok(entradas);

        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                    "error", e.getReason()
            ));
        }
    }

    @GetMapping("/niño/{niñoId}/entradas/mes")
    public ResponseEntity<?> obtenerEntradasPorMes(
            @PathVariable Long niñoId,
            @RequestParam int año,
            @RequestParam int mes) {

        try {
            List<DiarioEmocionalDTO> entradas = diarioEmocionalService
                    .obtenerEntradasPorMes(niñoId, año, mes);

            if (entradas.isEmpty()) {
                return ResponseEntity.ok()
                        .body(Map.of(
                                "message", "No se encontraron entradas para el mes especificado",
                                "entradas", List.of(),
                                "año", año,
                                "mes", mes
                        ));
            }

            return ResponseEntity.ok(entradas);

        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                    "error", e.getReason()
            ));
        }
    }

    @GetMapping("/niño/{niñoId}/entradas/existen")
    public ResponseEntity<?> verificarExistenciaEntradas(
            @PathVariable Long niñoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            boolean existen = diarioEmocionalService.existenEntradasEnRango(niñoId, startDate, endDate);
            return ResponseEntity.ok().body(Map.of("existenEntradas", existen));

        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                    "error", e.getReason()
            ));
        }
    }

    @GetMapping("/niño/{niñoId}")
    public ResponseEntity<List<DiarioEmocionalDTO>> getAllEmocionesByNiñoId(@PathVariable Long niñoId) {
        List<DiarioEmocionalDTO> emociones = diarioEmocionalService.findAllByNiñoId(niñoId);
        return ResponseEntity.ok(emociones);
    }
}