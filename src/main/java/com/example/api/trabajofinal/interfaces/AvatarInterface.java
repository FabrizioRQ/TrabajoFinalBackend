package com.example.api.trabajofinal.interfaces;

import com.example.api.trabajofinal.DTO.AvatarDTO;
import java.util.List;
import java.util.Optional;

public interface AvatarInterface {
    AvatarDTO crearAvatar(AvatarDTO avatarDTO);
    List<AvatarDTO> obtenerTodosLosAvatares();
    Optional<AvatarDTO> obtenerAvatarPorId(Long id);
    AvatarDTO actualizarAvatar(Long id, AvatarDTO avatarDTO);
    void eliminarAvatar(Long id);
}