package com.example.api.trabajofinal.services;

import com.example.api.trabajofinal.DTO.PadreDTO;
import com.example.api.trabajofinal.entities.Padre;
import com.example.api.trabajofinal.interfaces.PadreInterface;
import com.example.api.trabajofinal.repositories.PadreRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PadreService implements PadreInterface {

    @Autowired
    private PadreRepository padreRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PadreDTO crearPadre(PadreDTO padreDTO) {
        Padre padre = modelMapper.map(padreDTO, Padre.class);
        Padre padreGuardado = padreRepository.save(padre);
        return modelMapper.map(padreGuardado, PadreDTO.class);
    }

    @Override
    public List<PadreDTO> obtenerTodosLosPadres() {
        return padreRepository.findAll()
                .stream()
                .map(padre -> modelMapper.map(padre, PadreDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PadreDTO> obtenerPadrePorId(Long id) {
        return padreRepository.findById(id)
                .map(padre -> modelMapper.map(padre, PadreDTO.class));
    }

    @Override
    public PadreDTO actualizarPadre(Long id, PadreDTO padreDTO) {
        Optional<Padre> padreExistente = padreRepository.findById(id);
        if (padreExistente.isPresent()) {
            Padre padre = padreExistente.get();
            modelMapper.map(padreDTO, padre);
            Padre padreActualizado = padreRepository.save(padre);
            return modelMapper.map(padreActualizado, PadreDTO.class);
        }
        throw new RuntimeException("Padre no encontrado con ID: " + id);
    }

    @Override
    public void eliminarPadre(Long id) {
        if (padreRepository.existsById(id)) {
            padreRepository.deleteById(id);
        } else {
            throw new RuntimeException("Padre no encontrado con ID: " + id);
        }
    }

    @Override
    public List<PadreDTO> buscarPorNombre(String nombre) {
        return padreRepository.findByNombre(nombre)
                .stream()
                .map(p -> modelMapper.map(p, PadreDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PadreDTO> buscarPorApellidoPrefijo(String prefijo) {
        return padreRepository.findByApellidoStartingWith(prefijo)
                .stream()
                .map(p -> modelMapper.map(p, PadreDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> reportePadresConCantidadNiños() {
        return padreRepository.reportePadresConCantidadNiños()
                .stream()
                .map(r -> "Padre: " + r[0] + " " + r[1] + " | Hijos: " + r[2])
                .collect(Collectors.toList());
    }

    @Override
    public List<PadreDTO> padresConNiñosMenores() {
        return padreRepository.padresConNiñosMenores()
                .stream()
                .map(p -> modelMapper.map(p, PadreDTO.class))
                .collect(Collectors.toList());
    }
}