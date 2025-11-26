package com.example.api.trabajofinal.interfaces;

import com.example.api.trabajofinal.DTO.UsuarioDTO;
import com.example.api.trabajofinal.entities.Usuario;
import com.example.api.trabajofinal.security.entities.PasswordResetToken;

import java.util.List;

public interface UsuarioInterface {
    public UsuarioDTO registrarConContraseña(UsuarioDTO usuarioDTO, String contraseñaPlana);
    public boolean restablecerConToken(String token, String nuevaContraseña);
    public UsuarioDTO obtenerUsuario(Long idUsuario);
    public UsuarioDTO obtenerPorCorreo(String correo);
    public boolean existeUsuarioPorCorreo(String correo);
    public PasswordResetToken crearTokenParaUsuario(Usuario usuario);
    public UsuarioDTO obtenerMiPerfil(String correoUsuarioAutenticado);
    public UsuarioDTO actualizarMiPerfil(String correoUsuarioAutenticado, UsuarioDTO usuarioDTO);
    public List<UsuarioDTO> listar();
}
