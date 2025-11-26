package com.example.api.trabajofinal.controllers;

import com.example.api.trabajofinal.DTO.NiñoDTO;
import com.example.api.trabajofinal.DTO.AvatarDTO;
import com.example.api.trabajofinal.entities.Niño;
import com.example.api.trabajofinal.services.NiñoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/niños")
@CrossOrigin(origins = "http://localhost:4200")
public class NiñoController {

    @Autowired
    private NiñoService niñoService;

    @PostMapping
    public ResponseEntity<?> registrarNiño(@RequestBody NiñoDTO niñoDTO) {
        try {
            NiñoDTO niñoRegistrado = niñoService.registrarNiño(niñoDTO);
            return ResponseEntity.ok(niñoRegistrado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','PSICOLOGO','ADMIN')")
    public ResponseEntity<?> obtenerNiño(@PathVariable Long id) {
        Optional<NiñoDTO> niño = niñoService.obtenerNiñoPorId(id);
        if (niño.isPresent()) {
            return ResponseEntity.ok(niño.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{niñoId}/avatares-desbloqueados")
    public ResponseEntity<?> consultarAvataresDesbloqueadosPorNiño(@PathVariable Long niñoId) {
        try {
            List<AvatarDTO> avatares = niñoService.consultarAvataresDesbloqueadosPorNiñoId(niñoId);

            if (avatares.isEmpty()) {
                return ResponseEntity.ok()
                        .body("{\"message\": \"El niño no tiene avatares desbloqueados\", \"avatares\": []}");
            }

            return ResponseEntity.ok(avatares);

        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @GetMapping("/usuario/{usuarioId}/avatares-desbloqueados")
    public ResponseEntity<?> consultarAvataresDesbloqueadosPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<AvatarDTO> avatares = niñoService.consultarAvataresDesbloqueadosPorUsuarioId(usuarioId);

            if (avatares.isEmpty()) {
                return ResponseEntity.ok()
                        .body("{\"message\": \"El usuario no tiene avatares desbloqueados\", \"avatares\": []}");
            }

            return ResponseEntity.ok(avatares);

        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @GetMapping("/usuario/{usuarioId}/tiene-avatares")
    public ResponseEntity<?> verificarAvataresDesbloqueados(@PathVariable Long usuarioId) {
        try {
            boolean tieneAvatares = niñoService.tieneAvataresDesbloqueados(usuarioId);
            return ResponseEntity.ok().body("{\"tieneAvatares\": " + tieneAvatares + "}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"Error al verificar avatares: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/por-usuario/{usuarioId}")
    public ResponseEntity<?> obtenerNiñoPorUsuarioId(@PathVariable Long usuarioId) {
        try {
            Optional<NiñoDTO> niño = niñoService.obtenerNiñoPorUsuarioId(usuarioId);
            if (niño.isPresent()) {
                return ResponseEntity.ok(niño.get());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/psicologo/{idPsicologo}")
    public ResponseEntity<List<NiñoDTO>> getNiñosPorPsicologo(@PathVariable Long idPsicologo) {
        List<NiñoDTO> niños = niñoService.findByPsicologoId(idPsicologo);
        return ResponseEntity.ok(niños);
    }

    @GetMapping("/emociones/rango")
    public ResponseEntity<List<Niño>> getNiñosConEmocionesEnRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<Niño> niños = niñoService.findNiñosConEmocionesEnRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(niños);
    }

    @GetMapping("/psicologo/{idPsicologo}/conteo-registros")
    public ResponseEntity<List<Map<String, Object>>> getNiñosConConteoRegistros(@PathVariable Long idPsicologo) {
        List<Map<String, Object>> resultados = niñoService.findNiñosConConteoRegistrosByPsicologo(idPsicologo);
        return ResponseEntity.ok(resultados);
    }


    @GetMapping("/reportes/estadisticas-emociones")
    public ResponseEntity<List<Map<String, Object>>> getEstadisticasEmociones(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<Map<String, Object>> estadisticas = niñoService.getEstadisticasEmocionesPorNiño(fechaInicio, fechaFin);
        return ResponseEntity.ok(estadisticas);
    }


    @GetMapping("/reportes/dashboard/{idPsicologo}")
    public ResponseEntity<List<Map<String, Object>>> getDashboardNiños(@PathVariable Long idPsicologo) {
        List<Map<String, Object>> dashboard = niñoService.getDashboardNiñosPorPsicologo(idPsicologo);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping
    public ResponseEntity<List<NiñoDTO>> getAllNiños() {
        List<NiñoDTO> niños = niñoService.findAllNiños();
        return ResponseEntity.ok(niños);
    }
}