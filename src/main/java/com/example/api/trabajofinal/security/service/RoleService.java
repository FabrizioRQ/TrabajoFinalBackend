package com.example.api.trabajofinal.services;

import com.example.api.trabajofinal.security.entities.Role;
import com.example.api.trabajofinal.entities.Usuario;
import com.example.api.trabajofinal.security.repositories.RoleRepository;
import com.example.api.trabajofinal.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public int assignRoleToUser(Long usuarioId, Long roleId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        Role role = roleRepository.findById(roleId).orElse(null);

        if (usuario != null && role != null) {
            usuario.getRoles().add(role);
            usuarioRepository.save(usuario);
            return 1;
        }
        return 0;
    }
}