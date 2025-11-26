package com.example.api.trabajofinal.services;

import com.example.api.trabajofinal.DTO.PsicologoDTO;
import com.example.api.trabajofinal.entities.Psicologo;
import com.example.api.trabajofinal.entities.Usuario;
import com.example.api.trabajofinal.interfaces.PsicologoInterface;
import com.example.api.trabajofinal.repositories.PsicologoRepository;
import com.example.api.trabajofinal.repositories.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PsicologoService implements PsicologoInterface {

    @Autowired
    private PsicologoRepository psicologoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PsicologoDTO registrarPsicologo(PsicologoDTO psicologoDTO) {
        // Validar que el usuario exista
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(psicologoDTO.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + psicologoDTO.getIdUsuario());
        }

        Usuario usuario = usuarioOpt.get();

        // Validar que el usuario esté activo (estado "ACTIVE")
        if (!"ACTIVE".equals(usuario.getEstado())) {
            throw new RuntimeException("El usuario no está activo. Estado actual: " + usuario.getEstado());
        }

        // Validar que el usuario no tenga ya el rol de psicólogo
        if (psicologoRepository.existsByIdUsuario(psicologoDTO.getIdUsuario())) {
            throw new RuntimeException("El usuario ya tiene asignado el rol de psicólogo");
        }

        // Validar que no exista otro psicólogo con el mismo número de colegiatura
        if (psicologoRepository.existsByNumeroColegiatura(psicologoDTO.getNumeroColegiatura())) {
            throw new RuntimeException("Ya existe un psicólogo con el número de colegiatura: " + psicologoDTO.getNumeroColegiatura());
        }

        // Validar datos adicionales (licencia profesional, especialidad)
        if (psicologoDTO.getEspecialidad() == null || psicologoDTO.getEspecialidad().trim().isEmpty()) {
            throw new RuntimeException("La especialidad es requerida");
        }

        if (psicologoDTO.getNumeroColegiatura() == null || psicologoDTO.getNumeroColegiatura().trim().isEmpty()) {
            throw new RuntimeException("El número de colegiatura es requerido");
        }

        usuario.setTipoUsuario("PSICÓLOGO");
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        Psicologo psicologo = modelMapper.map(psicologoDTO, Psicologo.class);
        psicologo.setIdUsuario(usuarioActualizado);

        Psicologo psicologoGuardado = psicologoRepository.save(psicologo);

        System.out.println("=== PSICÓLOGO REGISTRADO ===");
        System.out.println("Usuario: " + usuarioActualizado.getCorreoElectronico());
        System.out.println("Psicólogo ID: " + psicologoGuardado.getId());
        System.out.println("Especialidad: " + psicologoGuardado.getEspecialidad());
        System.out.println("Colegiatura: " + psicologoGuardado.getNumeroColegiatura());
        System.out.println("=============================");

        return modelMapper.map(psicologoGuardado, PsicologoDTO.class);
    }

    @Override
    public Optional<PsicologoDTO> obtenerPsicologoPorId(Long id) {
        return psicologoRepository.findById(id)
                .map(psicologo -> modelMapper.map(psicologo, PsicologoDTO.class));
    }

    @Override
    public List<PsicologoDTO> obtenerPsicologos() {
        return psicologoRepository.findAll().stream()
                .map(p ->modelMapper.map(p, PsicologoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PsicologoDTO> buscarPorEspecialidad(String especialidad) {
        return psicologoRepository.findByEspecialidad(especialidad)
                .stream()
                .map(p -> modelMapper.map(p, PsicologoDTO.class))
                .toList();
    }

    @Override
    public PsicologoDTO buscarPorNumeroColegiatura(String numeroColegiatura) {
        return modelMapper.map(psicologoRepository.findByNumeroColegiatura(numeroColegiatura), PsicologoDTO.class);
    }

    @Override
    public List<PsicologoDTO> buscarPorNombreYEspecialidad(String nombre, String especialidad) {
        return psicologoRepository.buscarPorNombreYEspecialidad(nombre, especialidad)
                .stream()
                .map(p -> modelMapper.map(p, PsicologoDTO.class))
                .toList();
    }

    @Override
    public List<PsicologoDTO> listarConUsuario() {
        return psicologoRepository.findAllWithUsuario()
                .stream()
                .map(p -> modelMapper.map(p, PsicologoDTO.class))
                .toList();
    }
}