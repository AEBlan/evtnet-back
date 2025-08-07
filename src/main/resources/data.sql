/* Datos iniciales 
INSERT INTO clientes (nombre, email)
VALUES ('Cliente de Prueba 1', 'prueba@evtnet.com');*/

INSERT INTO usuario (
  nombre, apellido, username, dni, mail,
  fecha_nacimiento, foto_perfil, contrasena, CBU,
  fecha_hora_alta, fecha_hora_baja, usuario1_id
) VALUES (
  'Sara', 'Economia', 'saraeconomia', '12345678', 'sara@evtnet.com',
  '2000-01-01 00:00:00', 'foto.jpg', 'clave123', '001122334455667788',
  NOW(), NULL, NULL
);
