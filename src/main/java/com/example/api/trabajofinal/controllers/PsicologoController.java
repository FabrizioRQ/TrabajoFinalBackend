package com.example.api.trabajofinal.controllers;

import com.example.api.trabajofinal.DTO.PsicologoDTO;
import com.example.api.trabajofinal.services.PsicologoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/psicologos")
public class PsicologoController {

    @Autowired
    private PsicologoService psicologoService;

    @PostMapping
    public ResponseEntity<?> registrarPsicologo(@RequestBody PsicologoDTO psicologoDTO) {
        try {
            PsicologoDTO psicologoRegistrado = psicologoService.registrarPsicologo(psicologoDTO);
            return ResponseEntity.ok(psicologoRegistrado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<PsicologoDTO>> listarPsicologos() {
        List<PsicologoDTO> lista = psicologoService.obtenerPsicologos();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPsicologo(@PathVariable Long id) {
        Optional<PsicologoDTO> psicologo = psicologoService.obtenerPsicologoPorId(id);
        if (psicologo.isPresent()) {
            return ResponseEntity.ok(psicologo.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/especialidad/{especialidad}")
    public List<PsicologoDTO> buscarPorEspecialidad(@PathVariable String especialidad) {
        return psicologoService.buscarPorEspecialidad(especialidad);
    }

    @GetMapping("/colegiatura/{numero}")
    public PsicologoDTO buscarPorNumeroColegiatura(@PathVariable String numero) {
        return psicologoService.buscarPorNumeroColegiatura(numero);
    }

    @GetMapping("/buscar")
    public List<PsicologoDTO> buscarPorNombreYEspecialidad(
            @RequestParam String nombre,
            @RequestParam(required = false) String especialidad
    ) {
        return psicologoService.buscarPorNombreYEspecialidad(nombre, especialidad);
    }


    @GetMapping("/con-usuario")
    public List<PsicologoDTO> listarConUsuario() {
        return psicologoService.listarConUsuario();
    }

}