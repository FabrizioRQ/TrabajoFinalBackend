package com.example.api.trabajofinal.interfaces;

import com.example.api.trabajofinal.DTO.NiñoDTO;
import com.example.api.trabajofinal.entities.Niño;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NiñoInterface {
    public NiñoDTO registrarNiño(NiñoDTO niñoDTO);
    public Optional<NiñoDTO> obtenerNiñoPorId(Long id);
    // Consulta Simple
    List<NiñoDTO> findByPsicologoId(Long idPsicologo);

    // Consultas Complejas
    List<Niño> findNiñosConEmocionesEnRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);
    List<Map<String, Object>> findNiñosConConteoRegistrosByPsicologo(Long idPsicologo);

    // Reportes Complejos
    List<Map<String, Object>> getEstadisticasEmocionesPorNiño(LocalDate fechaInicio, LocalDate fechaFin);
    List<Map<String, Object>> getDashboardNiñosPorPsicologo(Long idPsicologo);
}