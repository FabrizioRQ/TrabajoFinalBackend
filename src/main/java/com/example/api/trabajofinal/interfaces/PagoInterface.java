package com.example.api.trabajofinal.interfaces;

import com.example.api.trabajofinal.DTO.PagoCreateDTO;
import com.example.api.trabajofinal.DTO.PagoDTO;

import java.util.List;

public interface PagoInterface {
    public PagoDTO crearPago(PagoCreateDTO dto);
    public PagoDTO obtenerPagoPorId(Long id);
    public List<PagoDTO> listarPagos();
    public List<PagoDTO> listarPagosPorUsuario(Long usuarioId);
    public PagoDTO actualizarPago(Long id, PagoCreateDTO dto);
    public void eliminarPago(Long id);
}
