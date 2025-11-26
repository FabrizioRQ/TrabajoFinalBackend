package com.example.api.trabajofinal.security.controller;

import com.example.api.trabajofinal.security.entities.Role;
import com.example.api.trabajofinal.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        try {
            Role savedRole = roleService.saveRole(role);
            return ResponseEntity.ok(savedRole);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear rol: " + e.getMessage());
        }
    }

    @PostMapping("/asignar/{usuarioId}/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignRoleToUser(@PathVariable Long usuarioId, @PathVariable Long roleId) {
        try {
            int result = roleService.assignRoleToUser(usuarioId, roleId);
            if (result == 1) {
                return ResponseEntity.ok("Rol asignado exitosamente");
            } else {
                return ResponseEntity.badRequest().body("Usuario o rol no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al asignar rol: " + e.getMessage());
        }
    }
}