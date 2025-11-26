package com.example.api.trabajofinal.controllers;

import com.example.api.trabajofinal.DTO.AvatarDTO;
import com.example.api.trabajofinal.services.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/avatares")
@CrossOrigin("*")
public class AvatarController {

    @Autowired
    private AvatarService avatarService;

    @PostMapping
    public ResponseEntity<AvatarDTO> crearAvatar(@RequestBody AvatarDTO avatarDTO) {
        AvatarDTO avatarCreado = avatarService.crearAvatar(avatarDTO);
        return ResponseEntity.ok(avatarCreado);
    }

    @GetMapping
    public ResponseEntity<List<AvatarDTO>> obtenerTodosLosAvatares() {
        List<AvatarDTO> avatares = avatarService.obtenerTodosLosAvatares();
        return ResponseEntity.ok(avatares);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvatarDTO> obtenerAvatarPorId(@PathVariable Long id) {
        Optional<AvatarDTO> avatar = avatarService.obtenerAvatarPorId(id);
        return avatar.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvatarDTO> actualizarAvatar(@PathVariable Long id, @RequestBody AvatarDTO avatarDTO) {
        try {
            AvatarDTO avatarActualizado = avatarService.actualizarAvatar(id, avatarDTO);
            return ResponseEntity.ok(avatarActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAvatar(@PathVariable Long id) {
        try {
            avatarService.eliminarAvatar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}