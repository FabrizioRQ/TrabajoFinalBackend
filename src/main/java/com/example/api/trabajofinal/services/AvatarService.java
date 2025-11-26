package com.example.api.trabajofinal.services;

import com.example.api.trabajofinal.DTO.AvatarDTO;
import com.example.api.trabajofinal.entities.Avatar;
import com.example.api.trabajofinal.interfaces.AvatarInterface;
import com.example.api.trabajofinal.repositories.AvatarRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AvatarService implements AvatarInterface {

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AvatarDTO crearAvatar(AvatarDTO avatarDTO) {
        Avatar avatar = modelMapper.map(avatarDTO, Avatar.class);
        Avatar avatarGuardado = avatarRepository.save(avatar);
        return modelMapper.map(avatarGuardado, AvatarDTO.class);
    }

    @Override
    public List<AvatarDTO> obtenerTodosLosAvatares() {
        return avatarRepository.findAll()
                .stream()
                .map(avatar -> modelMapper.map(avatar, AvatarDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AvatarDTO> obtenerAvatarPorId(Long id) {
        return avatarRepository.findById(id)
                .map(avatar -> modelMapper.map(avatar, AvatarDTO.class));
    }

    @Override
    public AvatarDTO actualizarAvatar(Long id, AvatarDTO avatarDTO) {
        Optional<Avatar> avatarExistente = avatarRepository.findById(id);
        if (avatarExistente.isPresent()) {
            Avatar avatar = avatarExistente.get();
            modelMapper.map(avatarDTO, avatar);
            Avatar avatarActualizado = avatarRepository.save(avatar);
            return modelMapper.map(avatarActualizado, AvatarDTO.class);
        }
        throw new RuntimeException("Avatar no encontrado con ID: " + id);
    }

    @Override
    public void eliminarAvatar(Long id) {
        if (avatarRepository.existsById(id)) {
            avatarRepository.deleteById(id);
        } else {
            throw new RuntimeException("Avatar no encontrado con ID: " + id);
        }
    }
}