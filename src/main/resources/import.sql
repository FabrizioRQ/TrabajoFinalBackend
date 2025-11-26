INSERT INTO roles (id, name) VALUES
                                 (1, 'ADMIN'),
                                 (2, 'USER'),
                                 (3, 'PSICÓLOGO');


INSERT INTO usuario (id_usuario, correo_electronico, contraseña, tipo_usuario, estado, nombre_completo)
VALUES (
           1,
           'admin@monstruosamigos.com',
           '$2a$10$8.UnVuG9HHgffUDAlk8qfOuWp6V3l0x7U0.8bYrJ.8nJ.8nJ.8nJ',
           'ADMIN',
           'ACTIVE',
           'Administrador del Sistema'
       );

INSERT INTO usuario_roles (usuario_id, role_id) VALUES (1, 1);


ALTER SEQUENCE usuario_id_usuario_seq RESTART WITH 2;