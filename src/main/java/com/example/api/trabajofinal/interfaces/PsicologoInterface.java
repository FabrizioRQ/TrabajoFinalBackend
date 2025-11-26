package com.example.api.trabajofinal.interfaces;

import com.example.api.trabajofinal.DTO.PsicologoDTO;


import java.util.List;
import java.util.Optional;

public interface PsicologoInterface {
    public PsicologoDTO registrarPsicologo(PsicologoDTO psicologoDTO);
    public Optional<PsicologoDTO> obtenerPsicologoPorId(Long id);
    public List<PsicologoDTO> obtenerPsicologos();
    public List<PsicologoDTO> buscarPorEspecialidad(String especialidad);
    public PsicologoDTO buscarPorNumeroColegiatura(String numeroColegiatura);
    List<PsicologoDTO> buscarPorNombreYEspecialidad(String nombre, String especialidad);
    List<PsicologoDTO> listarConUsuario();
}
