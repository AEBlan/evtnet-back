START TRANSACTION;
-- =======================
-- Usuarios
-- =======================
INSERT INTO usuario (nombre, apellido, username, dni, mail, fecha_nacimiento, contrasena, cbu, fecha_hora_alta)
SELECT 'Sergio','Albino','sergioalbino','12345678','sergio@example.com','1990-01-01 00:00:00','$2a$10$Y85YHk7oewaB7jgFBHMR1uUs.ek9VlJ3n1VOjhvZt77S1xoVVRLKe','0000000000000000000000', NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE username='sergioalbino');

INSERT INTO usuario (nombre, apellido, username, dni, mail, fecha_nacimiento, contrasena, cbu, fecha_hora_alta)
SELECT 'Admin','Eventos','adminevt','99999999','admin@example.com','1985-05-05 00:00:00','$2a$10$Y85YHk7oewaB7jgFBHMR1uUs.ek9VlJ3n1VOjhvZt77S1xoVVRLKe','2222222222222222222222', NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE username='adminevt');

SET @u_user   := (SELECT id FROM usuario WHERE username='sergioalbino' LIMIT 1);
SET @u_admin := (SELECT id FROM usuario WHERE username='adminevt'      LIMIT 1);

-- Roles de ejemplo
INSERT INTO rol_usuario (fecha_hora_alta, rol_id, usuario_id)
SELECT NOW(), (SELECT id FROM rol WHERE nombre='Usuario'       LIMIT 1), @u_user
WHERE NOT EXISTS (
  SELECT 1 FROM rol_usuario WHERE usuario_id=@u_user AND rol_id=(SELECT id FROM rol WHERE nombre='Usuario' LIMIT 1)
);

INSERT INTO rol_usuario (fecha_hora_alta, rol_id, usuario_id)
SELECT NOW(), (SELECT id FROM rol WHERE nombre='Administrador' LIMIT 1), @u_admin
WHERE NOT EXISTS (
  SELECT 1 FROM rol_usuario WHERE usuario_id=@u_admin AND rol_id=(SELECT id FROM rol WHERE nombre='Administrador' LIMIT 1)
);

INSERT INTO usuario (nombre, apellido, username, dni, mail, fecha_nacimiento, contrasena, cbu, fecha_hora_alta)
SELECT 'Carolina','Suarez','carol','33333333','carol@example.com','1992-03-10 00:00:00',
       '$2a$10$Y85YHk7oewaB7jgFBHMR1uUs.ek9VlJ3n1VOjhvZt77S1xoVVRLKe','CBU0003', NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE username='carol');

INSERT INTO usuario (nombre, apellido, username, dni, mail, fecha_nacimiento, contrasena, cbu, fecha_hora_alta)
SELECT 'Luis','Pérez','luly','44444444','luly@example.com','1994-04-15 00:00:00',
       '$2a$10$Y85YHk7oewaB7jgFBHMR1uUs.ek9VlJ3n1VOjhvZt77S1xoVVRLKe','0011223344556677889900', NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE username='luly');

INSERT INTO usuario (nombre, apellido, username, dni, mail, fecha_nacimiento, contrasena, cbu, fecha_hora_alta)
SELECT 'Samuel','Rodriguez','sam','55555555','sam@example.com','1995-05-20 00:00:00',
       '$2a$10$Y85YHk7oewaB7jgFBHMR1uUs.ek9VlJ3n1VOjhvZt77S1xoVVRLKe',NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE username='sam');

SET @u_user1   := (SELECT id FROM usuario WHERE username='carol' LIMIT 1);
SET @u_admin1 := (SELECT id FROM usuario WHERE username='luly'      LIMIT 1);
SET @u_user2   := (SELECT id FROM usuario WHERE username='sam' LIMIT 1);

-- Roles de ejemplo
INSERT INTO rol_usuario (fecha_hora_alta, rol_id, usuario_id)
SELECT NOW(), (SELECT id FROM rol WHERE nombre='Usuario'       LIMIT 1), @u_user1
WHERE NOT EXISTS (
  SELECT 1 FROM rol_usuario WHERE usuario_id=@u_user1 AND rol_id=(SELECT id FROM rol WHERE nombre='Usuario' LIMIT 1)
);

INSERT INTO rol_usuario (fecha_hora_alta, rol_id, usuario_id)
SELECT NOW(), (SELECT id FROM rol WHERE nombre='Usuario'       LIMIT 1), @u_user2
WHERE NOT EXISTS (
  SELECT 1 FROM rol_usuario WHERE usuario_id=@u_user2 AND rol_id=(SELECT id FROM rol WHERE nombre='Usuario' LIMIT 1)
);

INSERT INTO rol_usuario (fecha_hora_alta, rol_id, usuario_id)
SELECT NOW(), (SELECT id FROM rol WHERE nombre='Administrador' LIMIT 1), @u_admin1
WHERE NOT EXISTS (
  SELECT 1 FROM rol_usuario WHERE usuario_id=@u_admin1 AND rol_id=(SELECT id FROM rol WHERE nombre='Administrador' LIMIT 1)
);

COMMIT;