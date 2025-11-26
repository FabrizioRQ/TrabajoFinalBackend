package com.example.api.trabajofinal.services;

import com.example.api.trabajofinal.DTO.NiñoDTO;
import com.example.api.trabajofinal.DTO.AvatarDTO;
import com.example.api.trabajofinal.entities.Avatar;
import com.example.api.trabajofinal.entities.Niño;
import com.example.api.trabajofinal.entities.Usuario;
import com.example.api.trabajofinal.entities.Padre;
import com.example.api.trabajofinal.entities.Psicologo;
import com.example.api.trabajofinal.interfaces.NiñoInterface;
import com.example.api.trabajofinal.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NiñoService implements NiñoInterface {

    @Autowired
    private NiñoRepository niñoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private PadreRepository padreRepository;

    @Autowired
    private PsicologoRepository psicologoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public NiñoDTO registrarNiño(NiñoDTO niñoDTO) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(niñoDTO.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + niñoDTO.getIdUsuario());
        }

        if (niñoRepository.existsByIdUsuario(niñoDTO.getIdUsuario())) {
            throw new RuntimeException("El usuario ya está asignado a otro niño");
        }

        Optional<Avatar> avatarOpt = avatarRepository.findById(niñoDTO.getIdAvatar());
        if (avatarOpt.isEmpty()) {
            throw new RuntimeException("Avatar no encontrado con ID: " + niñoDTO.getIdAvatar());
        }

        Optional<Psicologo> psicologoOpt = psicologoRepository.findById(niñoDTO.getIdPsicologo());
        if (psicologoOpt.isEmpty()) {
            throw new RuntimeException("Psicólogo no encontrado con ID: " + niñoDTO.getIdPsicologo());
        }

        long cantidadNiñosPorPsicologo = niñoRepository.countByIdPsicologo(niñoDTO.getIdPsicologo());
        if (cantidadNiñosPorPsicologo >= 5) {
            throw new RuntimeException("El psicólogo ya tiene el máximo de 5 niños asignados. No puede registrar más niños para este psicólogo.");
        }

        Optional<Padre> padreOpt = padreRepository.findById(niñoDTO.getIdPadre());
        if (padreOpt.isEmpty()) {
            throw new RuntimeException("Padre no encontrado con ID: " + niñoDTO.getIdPadre());
        }

        Niño niño = modelMapper.map(niñoDTO, Niño.class);
        niño.setIdUsuario(usuarioOpt.get());
        niño.setIdAvatar(avatarOpt.get());
        niño.setIdPsicologo(psicologoOpt.get());
        niño.setIdPadre(padreOpt.get());

        Niño niñoGuardado = niñoRepository.save(niño);

        System.out.println("=== NIÑO REGISTRADO EXITOSAMENTE ===");
        System.out.println("Niño ID: " + niñoGuardado.getId());
        System.out.println("Usuario: " + usuarioOpt.get().getCorreoElectronico());
        System.out.println("Psicólogo: " + psicologoOpt.get().getIdUsuario().getCorreoElectronico());
        System.out.println("Niños del psicólogo: " + (cantidadNiñosPorPsicologo + 1) + "/5");
        System.out.println("===================================");

        return modelMapper.map(niñoGuardado, NiñoDTO.class);
    }

    @Override
    public Optional<NiñoDTO> obtenerNiñoPorId(Long id) {
        return niñoRepository.findById(id)
                .map(niño -> modelMapper.map(niño, NiñoDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NiñoDTO> findByPsicologoId(Long idPsicologo) {
        List<Niño> niños = niñoRepository.findByPsicologoId(idPsicologo);

        return niños.stream()
                .map(niño -> modelMapper.map(niño, NiñoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Niño> findNiñosConEmocionesEnRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return niñoRepository.findNiñosConEmocionesEnRangoFechas(fechaInicio, fechaFin);
    }



    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findNiñosConConteoRegistrosByPsicologo(Long idPsicologo) {
        List<Object[]> results = niñoRepository.findNiñosConConteoRegistrosByPsicologo(idPsicologo);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("niño", result[0]); // Entidad Niño completa
            item.put("totalRegistros", result[1]); // Conteo de registros
            response.add(item);
        }

        return response;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getEstadisticasEmocionesPorNiño(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Object[]> results = niñoRepository.findEstadisticasEmocionesPorNiño(fechaInicio, fechaFin);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> estadistica = new HashMap<>();
            estadistica.put("idNiño", result[0]);
            estadistica.put("fechaNacimiento", result[1]);
            estadistica.put("nombreCompleto", result[2]);
            estadistica.put("emocionRegistrada", result[3]);
            estadistica.put("frecuencia", result[4]);
            response.add(estadistica);
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDashboardNiñosPorPsicologo(Long idPsicologo) {
        List<Object[]> results = niñoRepository.findDashboardNiñosPorPsicologo(idPsicologo);
        List<Map<String, Object>> dashboard = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> niñoInfo = new HashMap<>();
            niñoInfo.put("id", result[0]);
            niñoInfo.put("nombreNiño", result[1]);
            niñoInfo.put("fechaNacimiento", result[2]);
            niñoInfo.put("nombreAvatar", result[3]);
            niñoInfo.put("nombrePsicologo", result[4]);
            niñoInfo.put("idPadre", result[5]);
            niñoInfo.put("totalRegistros", result[6]);
            niñoInfo.put("ultimoRegistro", result[7]);
            dashboard.add(niñoInfo);
        }

        return dashboard;
    }

    public List<AvatarDTO> consultarAvataresDesbloqueadosPorNiñoId(Long niñoId) {
        // Verificar si el niño existe
        Optional<Niño> niñoOpt = niñoRepository.findById(niñoId);
        if (niñoOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Niño no encontrado con ID: " + niñoId);
        }

        Niño niño = niñoOpt.get();

        // Obtener el avatar principal del niño y mapearlo a DTO
        Avatar avatarPrincipal = niño.getIdAvatar();
        AvatarDTO avatarDTO = modelMapper.map(avatarPrincipal, AvatarDTO.class);

        // En una implementación futura, aquí agregarías más avatares desbloqueados
        // desde una tabla de relación niño-avatar

        return List.of(avatarDTO);
    }

    public List<AvatarDTO> consultarAvataresDesbloqueadosPorUsuarioId(Long usuarioId) {
        Optional<Niño> niñoOpt = niñoRepository.findByUsuarioId(usuarioId);
        if (niñoOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontró un niño asociado al usuario con ID: " + usuarioId);
        }

        Niño niño = niñoOpt.get();
        return consultarAvataresDesbloqueadosPorNiñoId(niño.getId());
    }

    public boolean tieneAvataresDesbloqueados(Long usuarioId) {
        try {
            List<AvatarDTO> avatares = consultarAvataresDesbloqueadosPorUsuarioId(usuarioId);
            return !avatares.isEmpty();
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw e;
        }
    }

    public Optional<NiñoDTO> obtenerNiñoPorUsuarioId(Long usuarioId) {
        return niñoRepository.findByUsuarioId(usuarioId)
                .map(niño -> modelMapper.map(niño, NiñoDTO.class));
    }

    public List<NiñoDTO> findAllNiños() {
        List<Niño> niños = niñoRepository.findAll();
        return niños.stream()
                .map(niño -> modelMapper.map(niño, NiñoDTO.class))
                .collect(Collectors.toList());
    }
}