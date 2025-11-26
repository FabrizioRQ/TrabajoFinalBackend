package com.example.api.trabajofinal.services;

import com.example.api.trabajofinal.DTO.UsuarioDTO;
import com.example.api.trabajofinal.security.entities.PasswordResetToken;
import com.example.api.trabajofinal.security.entities.Role;
import com.example.api.trabajofinal.security.repositories.RoleRepository;
import com.example.api.trabajofinal.entities.Usuario;
import com.example.api.trabajofinal.interfaces.UsuarioInterface;
import com.example.api.trabajofinal.repositories.UsuarioRepository;
import com.example.api.trabajofinal.security.service.TokenService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements UsuarioInterface {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    public boolean validarPassword(String password) {
        if (password == null) {
            return false;
        }
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    }

    @Override
    public UsuarioDTO registrarConContraseña(UsuarioDTO usuarioDTO, String contraseñaPlana) {
        if (usuarioRepository.findByCorreoElectronico(usuarioDTO.getCorreoElectronico()) != null) {
            throw new IllegalArgumentException("Correo registrado anteriormente");
        }
        if (!validarPassword(contraseñaPlana)) {
            throw new IllegalArgumentException("Contraseña no cumple los requisitos de seguridad");
        }

        Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);
        usuario.setContraseña(passwordEncoder.encode(contraseñaPlana));
        usuario.setEstado("ACTIVE");

        Role rolUser = roleRepository.findByName("USER");
        if (rolUser == null) {
            throw new RuntimeException("Rol USER no encontrado en la base de datos");
        }

        usuario.setRoles(Collections.singleton(rolUser));

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        System.out.println("USUARIO REGISTRADO CON ROL USER: " + usuarioDTO.getCorreoElectronico());

        return modelMapper.map(usuarioGuardado, UsuarioDTO.class);
    }

    @Override
    public boolean restablecerConToken(String token, String nuevaContraseña) {
        try {
            System.out.println("=== INTENTANDO RESTABLECER CON TOKEN ===");
            System.out.println("Token recibido: " + token);

            PasswordResetToken prt = tokenService.findPasswordResetToken(token);
            System.out.println("Token encontrado en BD: " + (prt != null));

            if (prt == null) {
                System.out.println("Token no encontrado en la base de datos");
                return false;
            }

            System.out.println("Token expira: " + prt.getExpiryDate());
            System.out.println ("Ahora: " + LocalDateTime.now());

            if (prt.getExpiryDate().isBefore(LocalDateTime.now())) {
                System.out.println("Token expirado");
                tokenService.deletePasswordResetToken(prt);
                return false;
            }

            Usuario usuario = prt.getUsuario();
            System.out.println("Usuario asociado: " + (usuario != null ? usuario.getCorreoElectronico() : "null"));

            if (usuario == null) {
                System.out.println("No hay usuario asociado al token");
                return false;
            }

            System.out.println("Validando nueva contraseña...");
            if (!validarPassword(nuevaContraseña)) {
                System.out.println("Contraseña no cumple requisitos");
                return false;
            }

            System.out.println("Encriptando nueva contraseña...");
            usuario.setContraseña(passwordEncoder.encode(nuevaContraseña));
            usuarioRepository.save(usuario);
            tokenService.deletePasswordResetToken(prt);

            System.out.println("=== CONTRASEÑA RESTABLECIDA EXITOSAMENTE ===");
            System.out.println("Para: " + usuario.getCorreoElectronico());
            return true;

        } catch (Exception e) {
            System.out.println("ERROR en restablecerConToken: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public UsuarioDTO obtenerUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            return null;
        }
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    @Override
    public UsuarioDTO obtenerPorCorreo(String correo) {
        Usuario usuario = usuarioRepository.findByCorreoElectronico(correo);
        if (usuario == null) {
            return null;
        }
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    @Override
    public boolean existeUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreoElectronico(correo) != null;
    }

    @Override
    public PasswordResetToken crearTokenParaUsuario(Usuario usuario) {
        return tokenService.createPasswordResetToken(usuario, 24);
    }

    @Override
    public UsuarioDTO obtenerMiPerfil(String correoUsuarioAutenticado) {
        try {
            System.out.println("SOLICITUD MI PERFIL: " + correoUsuarioAutenticado);

            Usuario usuario = usuarioRepository.findByCorreoElectronico(correoUsuarioAutenticado);
            if (usuario == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            System.out.println("PERFIL OBTENIDO: " + usuario.getCorreoElectronico());
            return modelMapper.map(usuario, UsuarioDTO.class);

        } catch (Exception e) {
            System.out.println("ERROR OBTENIENDO MI PERFIL: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public UsuarioDTO actualizarMiPerfil(String correoUsuarioAutenticado, UsuarioDTO usuarioDTO) {
        try {
            System.out.println("ACTUALIZANDO MI PERFIL: " + correoUsuarioAutenticado);

            Usuario usuarioExistente = usuarioRepository.findByCorreoElectronico(correoUsuarioAutenticado);
            if (usuarioExistente == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            if (usuarioDTO.getNombreCompleto() != null && !usuarioDTO.getNombreCompleto().isEmpty()) {
                if (!usuarioDTO.getNombreCompleto().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{1,100}$")) {
                    throw new IllegalArgumentException("El nombre solo puede contener letras y espacios (máximo 100 caracteres)");
                }
                usuarioExistente.setNombreCompleto(usuarioDTO.getNombreCompleto());
            }

            if (usuarioDTO.getCorreoElectronico() != null &&
                    !usuarioDTO.getCorreoElectronico().equals(usuarioExistente.getCorreoElectronico())) {
                throw new IllegalArgumentException("No se puede modificar el correo electrónico");
            }

            Usuario actualizado = usuarioRepository.save(usuarioExistente);
            System.out.println("PERFIL ACTUALIZADO EXITOSAMENTE: " + usuarioExistente.getCorreoElectronico());

            return modelMapper.map(actualizado, UsuarioDTO.class);

        } catch (IllegalArgumentException e) {
            System.out.println("ERROR DE VALIDACIÓN: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("ERROR ACTUALIZANDO PERFIL: " + e.getMessage());
            throw new RuntimeException("Error al actualizar el perfil: " + e.getMessage());
        }
    }

    @Override
    public List<UsuarioDTO> listar() {
        return usuarioRepository.findAll().stream()
                .map(l -> modelMapper.map(l,UsuarioDTO.class))
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> buscarPorInicioNombre(String inicio) {
        return usuarioRepository.findByNombreStartsWith(inicio)
                .stream()
                .map(u -> modelMapper.map(u, UsuarioDTO.class))
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> listarUsuariosPsicologos() {
        return usuarioRepository.findUsuariosQueSonPsicologos()
                .stream()
                .map(u -> modelMapper.map(u, UsuarioDTO.class))
                .collect(Collectors.toList());
    }


    public List<UsuarioDTO> listarPorRol(String rol) {
        return usuarioRepository.findUsuariosByRol(rol)
                .stream()
                .map(u -> modelMapper.map(u, UsuarioDTO.class))
                .collect(Collectors.toList());
    }

    public List<Usuario> busquedaAvanzada(
            String correo,
            String nombre,
            String tipo,
            String estado,
            String rol
    ) {
        return usuarioRepository.busquedaAvanzada(
                StringUtils.hasText(correo) ? correo : null,
                StringUtils.hasText(nombre) ? nombre : null,
                StringUtils.hasText(tipo) ? tipo : null,
                StringUtils.hasText(estado) ? estado : null,
                StringUtils.hasText(rol) ? rol : null
        );
    }
}