package com.example.api.trabajofinal.entities;

import com.example.api.trabajofinal.security.entities.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Long id;

    @Size(max = 36)
    @NotNull
    @Column(name = "correo_electronico", nullable = false, length = 36, unique = true)
    private String correoElectronico;

    @Size(max = 100)
    @NotNull
    @Column(name = "\"contraseña\"", nullable = false, length = 100)
    private String contraseña;

    @Size(max = 50)
    @NotNull
    @Column(name = "tipo_usuario", nullable = false, length = 50)
    private String tipoUsuario;

    @Size(max = 20)
    @NotNull
    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "ACTIVE";

    @Size(max = 100)
    @Column(name = "nombre_completo", length = 100)
    private String nombreCompleto;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}