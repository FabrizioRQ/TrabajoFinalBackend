package com.example.api.trabajofinal.controllers;

import com.example.api.trabajofinal.DTO.PadreDTO;
import com.example.api.trabajofinal.services.PadreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/padres")
@CrossOrigin("*")
public class PadreController {

    @Autowired
    private PadreService padreService;

    @PostMapping
    public ResponseEntity<PadreDTO> crearPadre(@RequestBody PadreDTO padreDTO) {
        PadreDTO padreCreado = padreService.crearPadre(padreDTO);
        return ResponseEntity.ok(padreCreado);
    }

    @GetMapping
    public ResponseEntity<List<PadreDTO>> obtenerTodosLosPadres() {
        List<PadreDTO> padres = padreService.obtenerTodosLosPadres();
        return ResponseEntity.ok(padres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PadreDTO> obtenerPadrePorId(@PathVariable Long id) {
        Optional<PadreDTO> padre = padreService.obtenerPadrePorId(id);
        return padre.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PadreDTO> actualizarPadre(@PathVariable Long id, @RequestBody PadreDTO padreDTO) {
        try {
            PadreDTO padreActualizado = padreService.actualizarPadre(id, padreDTO);
            return ResponseEntity.ok(padreActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPadre(@PathVariable Long id) {
        try {
            padreService.eliminarPadre(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔹 Consulta simple
    @GetMapping("/buscar/nombre/{nombre}")
    public List<PadreDTO> buscarPorNombre(@PathVariable String nombre) {
        return padreService.buscarPorNombre(nombre);
    }

    // 🔹 Consulta simple
    @GetMapping("/buscar/apellido/{prefijo}")
    public List<PadreDTO> buscarPorApellidoPrefijo(@PathVariable String prefijo) {
        return padreService.buscarPorApellidoPrefijo(prefijo);
    }

    // 🔹 Reporte complejo
    @GetMapping("/reporte/cantidad-hijos")
    public List<String> reporteCantidadHijosPorPadre() {
        return padreService.reportePadresConCantidadNiños();
    }

    // 🔹 Reporte complejo
    @GetMapping("/reporte/con-ninos-menores")
    public List<PadreDTO> padresConNiñosMenores() {
        return padreService.padresConNiñosMenores();
    }
}