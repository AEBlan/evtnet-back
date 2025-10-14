START TRANSACTION;
-- =======================
-- Usuarios
-- =======================
ALTER TABLE subespacio 
MODIFY COLUMN encargado_subespacio_id BIGINT NULL;

ALTER TABLE subespacio MODIFY COLUMN evento_id BIGINT NULL;

ALTER TABLE inscripcion 
MODIFY permitir_devolucion_completa BOOLEAN NOT NULL DEFAULT FALSE;


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

-- ===========================================================
-- ESPACIOS DE PRUEBA (PRIVADOS) con Encargados
-- ===========================================================

-- ========== Polideportivo UTN (sergioalbino) ==========
INSERT INTO espacio (nombre, descripcion, direccion_ubicacion, latitud_ubicacion, longitud_ubicacion,
                     requiere_aprobar_eventos, fecha_hora_alta, tipo_espacio_id)
SELECT 'Polideportivo UTN', 'Complejo con canchas múltiples', 'Rodríguez Peña 123', -32.9000, -68.8500, TRUE, NOW(),
       (SELECT id FROM tipo_espacio WHERE nombre='Privado')
WHERE NOT EXISTS (SELECT 1 FROM espacio WHERE nombre='Polideportivo UTN');

-- propietario
INSERT INTO administrador_espacio (fecha_hora_alta, usuario_id, espacio_id, tipo_administrador_espacio_id)
SELECT NOW(), u.id, e.id, (SELECT id FROM tipo_administrador_espacio WHERE nombre='Propietario')
FROM usuario u
JOIN espacio e ON e.nombre='Polideportivo UTN'
WHERE u.username='sergioalbino'
  AND NOT EXISTS (SELECT 1 FROM administrador_espacio WHERE usuario_id=u.id AND espacio_id=e.id);

-- estado Habilitado
INSERT INTO espacio_estado (fecha_hora_alta, estado_espacio_id, espacio_id)
SELECT NOW(), (SELECT id FROM estado_espacio WHERE nombre='Habilitado'), e.id
FROM espacio e
WHERE e.nombre='Polideportivo UTN'
  AND NOT EXISTS (SELECT 1 FROM espacio_estado WHERE espacio_id=e.id);

-- subespacio
INSERT INTO subespacio (nombre, descripcion, capacidad_maxima, fecha_hora_alta, espacio_id)
SELECT 'Cancha Fútbol 5', 'Césped sintético techado', 10, NOW(), e.id
FROM espacio e
WHERE e.nombre='Polideportivo UTN'
  AND NOT EXISTS (SELECT 1 FROM subespacio WHERE nombre='Cancha Fútbol 5' AND espacio_id=e.id);

-- encargado de subespacio
INSERT INTO encargado_subespacio (fecha_hora_alta, usuario_id, subespacio_id)
SELECT NOW(), u.id, s.id
FROM usuario u
JOIN subespacio s ON s.nombre='Cancha Fútbol 5'
WHERE u.username='sergioalbino'
  AND NOT EXISTS (
    SELECT 1 FROM encargado_subespacio es
    WHERE es.usuario_id=u.id AND es.subespacio_id=s.id
  );

-- asignar encargado al subespacio
UPDATE subespacio s
JOIN encargado_subespacio es ON es.subespacio_id=s.id
SET s.encargado_subespacio_id = es.id
WHERE s.nombre='Cancha Fútbol 5';

-- Cancha Fútbol 5 (Polideportivo UTN)
INSERT INTO configuracion_horario_espacio (dias_anteelacion, fecha_desde, fecha_hasta, subespacio_id)
SELECT 
  2,                            -- días de antelación requeridos
  NOW(),                        -- fecha desde
  DATE_ADD(NOW(), INTERVAL 2 YEAR),  -- fecha hasta
  s.id
FROM subespacio s
WHERE s.nombre = 'Cancha Fútbol 5'
  AND NOT EXISTS (
    SELECT 1 FROM configuracion_horario_espacio WHERE subespacio_id = s.id
  );

INSERT INTO horario_espacio (dia_semana, hora_desde, hora_hasta, precio_organizacion, adicional_por_inscripcion, configuracion_horario_espacio_id)
SELECT 
  'Lunes a Viernes', '09:00:00', '18:00:00', 1500.00, 0.10, c.id
FROM configuracion_horario_espacio c
JOIN subespacio s ON s.id = c.subespacio_id
WHERE s.nombre = 'Cancha Fútbol 5'
  AND NOT EXISTS (SELECT 1 FROM horario_espacio WHERE configuracion_horario_espacio_id = c.id);


-- disciplina
INSERT INTO disciplina_subespacio (disciplina_id, subespacio_id)
SELECT d.id, s.id
FROM disciplina d, subespacio s
WHERE d.nombre='Futbol' AND s.nombre='Cancha Fútbol 5'
  AND NOT EXISTS (SELECT 1 FROM disciplina_subespacio WHERE disciplina_id=d.id AND subespacio_id=s.id);



-- ========== Gimnasio Central (carol) ==========
INSERT INTO espacio (nombre, descripcion, direccion_ubicacion, latitud_ubicacion, longitud_ubicacion,
                     requiere_aprobar_eventos, fecha_hora_alta, tipo_espacio_id)
SELECT 'Gimnasio Central', 'Centro fitness con máquinas y salón de yoga', 'San Martín 250', -32.8900, -68.8600, TRUE, NOW(),
       (SELECT id FROM tipo_espacio WHERE nombre='Privado')
WHERE NOT EXISTS (SELECT 1 FROM espacio WHERE nombre='Gimnasio Central');

-- propietario
INSERT INTO administrador_espacio (fecha_hora_alta, usuario_id, espacio_id, tipo_administrador_espacio_id)
SELECT NOW(), u.id, e.id, (SELECT id FROM tipo_administrador_espacio WHERE nombre='Propietario')
FROM usuario u
JOIN espacio e ON e.nombre='Gimnasio Central'
WHERE u.username='carol'
  AND NOT EXISTS (SELECT 1 FROM administrador_espacio WHERE usuario_id=u.id AND espacio_id=e.id);

-- estado En revisión
INSERT INTO espacio_estado (fecha_hora_alta, estado_espacio_id, espacio_id)
SELECT NOW(), (SELECT id FROM estado_espacio WHERE nombre IN ('En_revisión','En Revision','En Revisión') LIMIT 1), e.id
FROM espacio e
WHERE e.nombre='Gimnasio Central'
  AND NOT EXISTS (SELECT 1 FROM espacio_estado WHERE espacio_id=e.id);

-- subespacio
INSERT INTO subespacio (nombre, descripcion, capacidad_maxima, fecha_hora_alta, espacio_id)
SELECT 'Salón de Yoga', 'Espacio para clases grupales', 20, NOW(), e.id
FROM espacio e
WHERE e.nombre='Gimnasio Central'
  AND NOT EXISTS (SELECT 1 FROM subespacio WHERE nombre='Salón de Yoga' AND espacio_id=e.id);

-- encargado de subespacio
INSERT INTO encargado_subespacio (fecha_hora_alta, usuario_id, subespacio_id)
SELECT NOW(), u.id, s.id
FROM usuario u
JOIN subespacio s ON s.nombre='Salón de Yoga'
WHERE u.username='carol'
  AND NOT EXISTS (
    SELECT 1 FROM encargado_subespacio es
    WHERE es.usuario_id=u.id AND es.subespacio_id=s.id
  );

-- asignar encargado al subespacio
UPDATE subespacio s
JOIN encargado_subespacio es ON es.subespacio_id=s.id
SET s.encargado_subespacio_id = es.id
WHERE s.nombre='Salón de Yoga';

-- Salón de Yoga (Gimnasio Central)
INSERT INTO configuracion_horario_espacio (dias_anteelacion, fecha_desde, fecha_hasta, subespacio_id)
SELECT 
  3,
  NOW(),
  DATE_ADD(NOW(), INTERVAL 2 YEAR),
  s.id
FROM subespacio s
WHERE s.nombre = 'Salón de Yoga'
  AND NOT EXISTS (
    SELECT 1 FROM configuracion_horario_espacio WHERE subespacio_id = s.id
  );

INSERT INTO horario_espacio (dia_semana, hora_desde, hora_hasta, precio_organizacion, adicional_por_inscripcion, configuracion_horario_espacio_id)
SELECT 
  'Lunes a Viernes', '08:00:00', '20:00:00', 1200.00, 0.10, c.id
FROM configuracion_horario_espacio c
JOIN subespacio s ON s.id = c.subespacio_id
WHERE s.nombre = 'Salón de Yoga'
  AND NOT EXISTS (SELECT 1 FROM horario_espacio WHERE configuracion_horario_espacio_id = c.id);


-- disciplina
INSERT INTO disciplina_subespacio (disciplina_id, subespacio_id)
SELECT d.id, s.id
FROM disciplina d, subespacio s
WHERE d.nombre='Padel' AND s.nombre='Salón de Yoga'
  AND NOT EXISTS (SELECT 1 FROM disciplina_subespacio WHERE disciplina_id=d.id AND subespacio_id=s.id);



-- ========== Club Deportivo Oeste (luly) ==========
INSERT INTO espacio (nombre, descripcion, direccion_ubicacion, latitud_ubicacion, longitud_ubicacion,
                     requiere_aprobar_eventos, fecha_hora_alta, tipo_espacio_id)
SELECT 'Club Deportivo Oeste', 'Predio con varias canchas exteriores', 'Belgrano 890', -32.9100, -68.8400, TRUE, NOW(),
       (SELECT id FROM tipo_espacio WHERE nombre='Privado')
WHERE NOT EXISTS (SELECT 1 FROM espacio WHERE nombre='Club Deportivo Oeste');

-- propietario
INSERT INTO administrador_espacio (fecha_hora_alta, usuario_id, espacio_id, tipo_administrador_espacio_id)
SELECT NOW(), u.id, e.id, (SELECT id FROM tipo_administrador_espacio WHERE nombre='Propietario')
FROM usuario u
JOIN espacio e ON e.nombre='Club Deportivo Oeste'
WHERE u.username='luly'
  AND NOT EXISTS (SELECT 1 FROM administrador_espacio WHERE usuario_id=u.id AND espacio_id=e.id);

-- estado Habilitado
INSERT INTO espacio_estado (fecha_hora_alta, estado_espacio_id, espacio_id)
SELECT NOW(), (SELECT id FROM estado_espacio WHERE nombre='Habilitado'), e.id
FROM espacio e
WHERE e.nombre='Club Deportivo Oeste'
  AND NOT EXISTS (SELECT 1 FROM espacio_estado WHERE espacio_id=e.id);

-- subespacio
INSERT INTO subespacio (nombre, descripcion, capacidad_maxima, fecha_hora_alta, espacio_id)
SELECT 'Cancha de Pádel', 'Cancha techada doble vidrio', 4, NOW(), e.id
FROM espacio e
WHERE e.nombre='Club Deportivo Oeste'
  AND NOT EXISTS (SELECT 1 FROM subespacio WHERE nombre='Cancha de Pádel' AND espacio_id=e.id);

-- encargado de subespacio
INSERT INTO encargado_subespacio (fecha_hora_alta, usuario_id, subespacio_id)
SELECT NOW(), u.id, s.id
FROM usuario u
JOIN subespacio s ON s.nombre='Cancha de Pádel'
WHERE u.username='luly'
  AND NOT EXISTS (
    SELECT 1 FROM encargado_subespacio es
    WHERE es.usuario_id=u.id AND es.subespacio_id=s.id
  );

-- asignar encargado al subespacio
UPDATE subespacio s
JOIN encargado_subespacio es ON es.subespacio_id=s.id
SET s.encargado_subespacio_id = es.id
WHERE s.nombre='Cancha de Pádel';

-- Cancha de Pádel (Club Deportivo Oeste)
INSERT INTO configuracion_horario_espacio (dias_anteelacion, fecha_desde, fecha_hasta, subespacio_id)
SELECT 
  1,
  NOW(),
  DATE_ADD(NOW(), INTERVAL 2 YEAR),
  s.id
FROM subespacio s
WHERE s.nombre = 'Cancha de Pádel'
  AND NOT EXISTS (
    SELECT 1 FROM configuracion_horario_espacio WHERE subespacio_id = s.id
  );

-- horario
INSERT INTO horario_espacio (dia_semana, hora_desde, hora_hasta, precio_organizacion, adicional_por_inscripcion, configuracion_horario_espacio_id)
SELECT 
  'Lunes a Viernes', '09:00:00', '23:00:00', 1800.00, 0.15, c.id
FROM configuracion_horario_espacio c
JOIN subespacio s ON s.id = c.subespacio_id
WHERE s.nombre = 'Cancha de Pádel'
  AND NOT EXISTS (SELECT 1 FROM horario_espacio WHERE configuracion_horario_espacio_id = c.id);

-- disciplina
INSERT INTO disciplina_subespacio (disciplina_id, subespacio_id)
SELECT d.id, s.id
FROM disciplina d, subespacio s
WHERE d.nombre='Padel' AND s.nombre='Cancha de Pádel'
  AND NOT EXISTS (SELECT 1 FROM disciplina_subespacio WHERE disciplina_id=d.id AND subespacio_id=s.id);


-- ===========================================================
-- EVENTO DE PRUEBA: “Partido amistoso”
-- ===========================================================

INSERT INTO evento (
    nombre, descripcion, fecha_hora_inicio, fecha_hora_fin,
    precio_inscripcion, cantidad_maxima_invitados, cantidad_maxima_participantes,
    precio_organizacion, adicional_por_inscripcion, subespacio_id
)
SELECT
    'Partido amistoso',
    'Encuentro informal de fútbol entre amigos.',
    DATE_ADD(NOW(), INTERVAL 1 DAY),                                -- inicio: mañana
    DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 2 HOUR),     -- fin: 2 horas después
    1000.00,
    5,
    10,
    1500.00,
    0.10,
    s.id
FROM subespacio s
WHERE s.nombre = 'Cancha Fútbol 5'
  AND NOT EXISTS (SELECT 1 FROM evento WHERE nombre = 'Partido amistoso');

-- ===========================================================
-- ADMINISTRADOR DEL EVENTO (Organizador)
-- ===========================================================
INSERT INTO administrador_evento (fecha_hora_alta, usuario_id, evento_id, tipo_administrador_evento_id)
SELECT NOW(),
       (SELECT id FROM usuario WHERE username='sergioalbino'),
       e.id,
       (SELECT id FROM tipo_administrador_evento WHERE nombre='Organizador')
FROM evento e
WHERE e.nombre='Partido amistoso'
  AND NOT EXISTS (
    SELECT 1 FROM administrador_evento ae
    WHERE ae.evento_id = e.id
      AND ae.usuario_id = (SELECT id FROM usuario WHERE username='sergioalbino')
  );



-- ===========================================================
-- ESTADO DEL EVENTO (Aceptado)
-- ===========================================================
INSERT INTO evento_estado (descripcion, fecha_hora_alta, evento_id, estado_evento_id)
SELECT 
    'Evento aprobado automáticamente para pruebas',
    NOW(),
    e.id,
    (SELECT id FROM estado_evento WHERE nombre='Aceptado')
FROM evento e
WHERE e.nombre='Partido amistoso'
  AND NOT EXISTS (
    SELECT 1 FROM evento_estado es
    WHERE es.evento_id = e.id
  );



-- ===========================================================
-- INSCRIPCIONES DE PRUEBA
-- ===========================================================
INSERT INTO inscripcion (evento_id, usuario_id, fecha_hora_alta)
SELECT e.id, u.id, NOW()
FROM evento e
JOIN usuario u ON u.username IN ('sergioalbino', 'carol', 'sam')
WHERE e.nombre='Partido amistoso'
  AND NOT EXISTS (
    SELECT 1 FROM inscripcion i
    WHERE i.evento_id=e.id AND i.usuario_id=u.id
  );


-- ===========================================================
-- CALIFICACIONES (USANDO MOTIVOS Y TIPOS EXISTENTES)
-- ===========================================================

-- ===========================================================
-- ✅ CALIFICACIÓN NORMAL: Sergio → Carol
-- ===========================================================

-- Crear la calificación si no existe
INSERT INTO calificacion (
    descripcion, fecha_hora, calificado_id, autor_id, calificacion_tipo_id
)
SELECT 
    'Excelente compañera, puntual y colaboradora.',
    NOW(),
    (SELECT id FROM usuario WHERE username='carol'),
    (SELECT id FROM usuario WHERE username='sergioalbino'),
    (SELECT id FROM calificacion_tipo WHERE nombre='Calificacion Normal')
WHERE NOT EXISTS (
  SELECT 1 FROM calificacion c
  WHERE c.calificado_id = (SELECT id FROM usuario WHERE username='carol')
    AND c.autor_id = (SELECT id FROM usuario WHERE username='sergioalbino')
);

-- Relación con motivo (ejemplo: "Puntual")
INSERT INTO calificacion_motivo_calificacion (calificacion_id, motivo_calificacion_id)
SELECT c.id, m.id
FROM calificacion c
JOIN motivo_calificacion m ON m.nombre='Puntual'
WHERE c.descripcion='Excelente compañera, puntual y colaboradora.'
  AND NOT EXISTS (
    SELECT 1 FROM calificacion_motivo_calificacion cm
    WHERE cm.calificacion_id = c.id AND cm.motivo_calificacion_id = m.id
  );

-- ===========================================================
-- 🚨 Calificación TIPO DENUNCIA: Carol → Sam
-- ===========================================================

INSERT INTO calificacion (
    descripcion, fecha_hora, calificado_id, autor_id, calificacion_tipo_id
)
SELECT 
    'Denuncia: conducta inapropiada durante el evento.',
    NOW(),
    (SELECT id FROM usuario WHERE username='sam'),
    (SELECT id FROM usuario WHERE username='carol'),
    (SELECT id FROM calificacion_tipo WHERE nombre='Calificacion Denuncia')
WHERE NOT EXISTS (
  SELECT 1 FROM calificacion c
  WHERE c.calificado_id = (SELECT id FROM usuario WHERE username='sam')
    AND c.autor_id = (SELECT id FROM usuario WHERE username='carol')
);

-- ❌ Sin relación con motivo ni tipo (por política de negocio)
-- → No se inserta en tablas intermedias

-- ===========================================================
-- 📍 NUEVO ESPACIO 1: Complejo Deportivo Norte (admin = carol)
-- ===========================================================

INSERT INTO espacio (nombre, descripcion, direccion_ubicacion, latitud_ubicacion, longitud_ubicacion,
                     requiere_aprobar_eventos, fecha_hora_alta, tipo_espacio_id)
SELECT 'Complejo Deportivo Norte', 'Centro deportivo techado con canchas y piscina', 
       'Av. San Martín Norte 450', -32.88, -68.84, TRUE, NOW(),
       (SELECT id FROM tipo_espacio WHERE nombre='Privado')
WHERE NOT EXISTS (SELECT 1 FROM espacio WHERE nombre='Complejo Deportivo Norte');

-- propietario
INSERT INTO administrador_espacio (fecha_hora_alta, usuario_id, espacio_id, tipo_administrador_espacio_id)
SELECT NOW(), u.id, e.id, (SELECT id FROM tipo_administrador_espacio WHERE nombre='Propietario')
FROM usuario u
JOIN espacio e ON e.nombre='Complejo Deportivo Norte'
WHERE u.username='carol'
  AND NOT EXISTS (SELECT 1 FROM administrador_espacio WHERE usuario_id=u.id AND espacio_id=e.id);

-- estado habilitado
INSERT INTO espacio_estado (fecha_hora_alta, estado_espacio_id, espacio_id)
SELECT NOW(), (SELECT id FROM estado_espacio WHERE nombre='Habilitado'), e.id
FROM espacio e
WHERE e.nombre='Complejo Deportivo Norte'
  AND NOT EXISTS (SELECT 1 FROM espacio_estado WHERE espacio_id=e.id);

-- subespacios: cancha + piscina
INSERT INTO subespacio (nombre, descripcion, capacidad_maxima, fecha_hora_alta, espacio_id)
SELECT 'Cancha Multiuso', 'Ideal para básquet y voley', 12, NOW(), e.id
FROM espacio e WHERE e.nombre='Complejo Deportivo Norte'
  AND NOT EXISTS (SELECT 1 FROM subespacio WHERE nombre='Cancha Multiuso' AND espacio_id=e.id);

INSERT INTO subespacio (nombre, descripcion, capacidad_maxima, fecha_hora_alta, espacio_id)
SELECT 'Piscina Olímpica', 'Piscina climatizada cubierta', 20, NOW(), e.id
FROM espacio e WHERE e.nombre='Complejo Deportivo Norte'
  AND NOT EXISTS (SELECT 1 FROM subespacio WHERE nombre='Piscina Olímpica' AND espacio_id=e.id);

-- encargados
INSERT INTO encargado_subespacio (fecha_hora_alta, usuario_id, subespacio_id)
SELECT NOW(), (SELECT id FROM usuario WHERE username='carol'), s.id
FROM subespacio s WHERE s.nombre IN ('Cancha Multiuso', 'Piscina Olímpica')
  AND NOT EXISTS (SELECT 1 FROM encargado_subespacio es WHERE es.usuario_id=(SELECT id FROM usuario WHERE username='carol') AND es.subespacio_id=s.id);

UPDATE subespacio s
JOIN encargado_subespacio es ON es.subespacio_id=s.id
SET s.encargado_subespacio_id = es.id
WHERE s.nombre IN ('Cancha Multiuso', 'Piscina Olímpica');

-- horarios
INSERT INTO configuracion_horario_espacio (dias_anteelacion, fecha_desde, fecha_hasta, subespacio_id)
SELECT 2, NOW(), DATE_ADD(NOW(), INTERVAL 2 YEAR), s.id
FROM subespacio s WHERE s.nombre IN ('Cancha Multiuso', 'Piscina Olímpica')
  AND NOT EXISTS (SELECT 1 FROM configuracion_horario_espacio WHERE subespacio_id=s.id);

INSERT INTO horario_espacio (dia_semana, hora_desde, hora_hasta, precio_organizacion, adicional_por_inscripcion, configuracion_horario_espacio_id)
SELECT 'Lunes a Sábado', '08:00:00', '22:00:00', 2000.00, 0.12, c.id
FROM configuracion_horario_espacio c
JOIN subespacio s ON s.id=c.subespacio_id
WHERE s.nombre IN ('Cancha Multiuso', 'Piscina Olímpica')
  AND NOT EXISTS (SELECT 1 FROM horario_espacio WHERE configuracion_horario_espacio_id=c.id);

-- disciplinas
INSERT INTO disciplina_subespacio (disciplina_id, subespacio_id)
SELECT d.id, s.id FROM disciplina d, subespacio s
WHERE d.nombre IN ('Futbol','Padel') AND s.nombre='Cancha Multiuso'
  AND NOT EXISTS (SELECT 1 FROM disciplina_subespacio WHERE disciplina_id=d.id AND subespacio_id=s.id);

INSERT INTO disciplina_subespacio (disciplina_id, subespacio_id)
SELECT d.id, s.id FROM disciplina d, subespacio s
WHERE d.nombre='Metegol' AND s.nombre='Piscina Olímpica'
  AND NOT EXISTS (SELECT 1 FROM disciplina_subespacio WHERE disciplina_id=d.id AND subespacio_id=s.id);



-- ===========================================================
-- 📍 NUEVO ESPACIO 2: Parque Sur (propietario = luly)
-- ===========================================================

INSERT INTO espacio (nombre, descripcion, direccion_ubicacion, latitud_ubicacion, longitud_ubicacion,
                     requiere_aprobar_eventos, fecha_hora_alta, tipo_espacio_id)
SELECT 'Parque Sur', 'Área recreativa abierta con quinchos y pista de atletismo', 
       'Ruta 40 Sur, Km 5', -32.92, -68.83, TRUE, NOW(),
       (SELECT id FROM tipo_espacio WHERE nombre='Privado')
WHERE NOT EXISTS (SELECT 1 FROM espacio WHERE nombre='Parque Sur');

INSERT INTO administrador_espacio (fecha_hora_alta, usuario_id, espacio_id, tipo_administrador_espacio_id)
SELECT NOW(), u.id, e.id, (SELECT id FROM tipo_administrador_espacio WHERE nombre='Propietario')
FROM usuario u JOIN espacio e ON e.nombre='Parque Sur'
WHERE u.username='luly'
  AND NOT EXISTS (SELECT 1 FROM administrador_espacio WHERE usuario_id=u.id AND espacio_id=e.id);

INSERT INTO espacio_estado (fecha_hora_alta, estado_espacio_id, espacio_id)
SELECT NOW(), (SELECT id FROM estado_espacio WHERE nombre='Habilitado'), e.id
FROM espacio e WHERE e.nombre='Parque Sur'
  AND NOT EXISTS (SELECT 1 FROM espacio_estado WHERE espacio_id=e.id);

INSERT INTO subespacio (nombre, descripcion, capacidad_maxima, fecha_hora_alta, espacio_id)
SELECT 'Pista de Atletismo', '400 metros con cronómetros automáticos', 30, NOW(), e.id
FROM espacio e WHERE e.nombre='Parque Sur'
  AND NOT EXISTS (SELECT 1 FROM subespacio WHERE nombre='Pista de Atletismo' AND espacio_id=e.id);

INSERT INTO encargado_subespacio (fecha_hora_alta, usuario_id, subespacio_id)
SELECT NOW(), (SELECT id FROM usuario WHERE username='luly'), s.id
FROM subespacio s WHERE s.nombre='Pista de Atletismo'
  AND NOT EXISTS (SELECT 1 FROM encargado_subespacio WHERE usuario_id=(SELECT id FROM usuario WHERE username='luly') AND subespacio_id=s.id);

UPDATE subespacio s
JOIN encargado_subespacio es ON es.subespacio_id=s.id
SET s.encargado_subespacio_id = es.id
WHERE s.nombre='Pista de Atletismo';

INSERT INTO configuracion_horario_espacio (dias_anteelacion, fecha_desde, fecha_hasta, subespacio_id)
SELECT 1, NOW(), DATE_ADD(NOW(), INTERVAL 3 YEAR), s.id
FROM subespacio s WHERE s.nombre='Pista de Atletismo'
  AND NOT EXISTS (SELECT 1 FROM configuracion_horario_espacio WHERE subespacio_id=s.id);

INSERT INTO horario_espacio (dia_semana, hora_desde, hora_hasta, precio_organizacion, adicional_por_inscripcion, configuracion_horario_espacio_id)
SELECT 'Martes a Domingo', '07:00:00', '20:00:00', 800.00, 0.08, c.id
FROM configuracion_horario_espacio c
JOIN subespacio s ON s.id=c.subespacio_id
WHERE s.nombre='Pista de Atletismo'
  AND NOT EXISTS (SELECT 1 FROM horario_espacio WHERE configuracion_horario_espacio_id=c.id);

INSERT INTO disciplina_subespacio (disciplina_id, subespacio_id)
SELECT d.id, s.id
FROM disciplina d, subespacio s
WHERE d.nombre='Futbol' AND s.nombre='Pista de Atletismo'
  AND NOT EXISTS (SELECT 1 FROM disciplina_subespacio WHERE disciplina_id=d.id AND subespacio_id=s.id);



-- ===========================================================
-- ⚽ NUEVOS EVENTOS DE PRUEBA
-- ===========================================================

-- 1️⃣ Torneo de Verano (en Complejo Deportivo Norte)
INSERT INTO evento (nombre, descripcion, fecha_hora_inicio, fecha_hora_fin,
                    precio_inscripcion, cantidad_maxima_invitados, cantidad_maxima_participantes,
                    precio_organizacion, adicional_por_inscripcion, subespacio_id)
SELECT 
    'Torneo de Verano',
    'Competencia amistosa de fútbol 5 en cancha multiuso.',
    DATE_ADD(NOW(), INTERVAL 3 DAY),
    DATE_ADD(DATE_ADD(NOW(), INTERVAL 3 DAY), INTERVAL 3 HOUR),
    1200.00, 5, 12, 2000.00, 0.10, s.id
FROM subespacio s WHERE s.nombre='Cancha Multiuso'
  AND NOT EXISTS (SELECT 1 FROM evento WHERE nombre='Torneo de Verano');

INSERT INTO administrador_evento (fecha_hora_alta, usuario_id, evento_id, tipo_administrador_evento_id)
SELECT NOW(), (SELECT id FROM usuario WHERE username='carol'), e.id,
       (SELECT id FROM tipo_administrador_evento WHERE nombre='Organizador')
FROM evento e WHERE e.nombre='Torneo de Verano'
  AND NOT EXISTS (SELECT 1 FROM administrador_evento WHERE evento_id=e.id);

INSERT INTO evento_estado (descripcion, fecha_hora_alta, evento_id, estado_evento_id)
SELECT 'Evento aprobado automáticamente para pruebas', NOW(), e.id,
       (SELECT id FROM estado_evento WHERE nombre='Aceptado')
FROM evento e WHERE e.nombre='Torneo de Verano'
  AND NOT EXISTS (SELECT 1 FROM evento_estado WHERE evento_id=e.id);

INSERT INTO inscripcion (evento_id, usuario_id, fecha_hora_alta)
SELECT e.id, u.id, NOW()
FROM evento e JOIN usuario u ON u.username IN ('carol','sam')
WHERE e.nombre='Torneo de Verano'
  AND NOT EXISTS (SELECT 1 FROM inscripcion i WHERE i.evento_id=e.id AND i.usuario_id=u.id);



-- 2️⃣ Carrera Saludable (en Parque Sur)
INSERT INTO evento (nombre, descripcion, fecha_hora_inicio, fecha_hora_fin,
                    precio_inscripcion, cantidad_maxima_invitados, cantidad_maxima_participantes,
                    precio_organizacion, adicional_por_inscripcion, subespacio_id)
SELECT 
    'Carrera Saludable',
    'Evento recreativo abierto al público en Pista de Atletismo.',
    DATE_ADD(NOW(), INTERVAL 5 DAY),
    DATE_ADD(DATE_ADD(NOW(), INTERVAL 5 DAY), INTERVAL 4 HOUR),
    500.00, 10, 50, 800.00, 0.05, s.id
FROM subespacio s WHERE s.nombre='Pista de Atletismo'
  AND NOT EXISTS (SELECT 1 FROM evento WHERE nombre='Carrera Saludable');

INSERT INTO administrador_evento (fecha_hora_alta, usuario_id, evento_id, tipo_administrador_evento_id)
SELECT NOW(), (SELECT id FROM usuario WHERE username='luly'), e.id,
       (SELECT id FROM tipo_administrador_evento WHERE nombre='Organizador')
FROM evento e WHERE e.nombre='Carrera Saludable'
  AND NOT EXISTS (SELECT 1 FROM administrador_evento WHERE evento_id=e.id);

INSERT INTO evento_estado (descripcion, fecha_hora_alta, evento_id, estado_evento_id)
SELECT 'Evento aprobado automáticamente para pruebas', NOW(), e.id,
       (SELECT id FROM estado_evento WHERE nombre='Aceptado')
FROM evento e WHERE e.nombre='Carrera Saludable'
  AND NOT EXISTS (SELECT 1 FROM evento_estado WHERE evento_id=e.id);

INSERT INTO inscripcion (evento_id, usuario_id, fecha_hora_alta)
SELECT e.id, u.id, NOW()
FROM evento e JOIN usuario u ON u.username IN ('sergioalbino','carol','sam')
WHERE e.nombre='Carrera Saludable'
  AND NOT EXISTS (SELECT 1 FROM inscripcion i WHERE i.evento_id=e.id AND i.usuario_id=u.id);



-- ===========================================================
-- 💬 CALIFICACIÓN EXTRA: Carol → Luly (normal positiva)
-- ===========================================================
INSERT INTO calificacion (descripcion, fecha_hora, calificado_id, autor_id, calificacion_tipo_id)
SELECT 'Excelente administración del evento, todo muy organizado.', NOW(),
       (SELECT id FROM usuario WHERE username='luly'),
       (SELECT id FROM usuario WHERE username='carol'),
       (SELECT id FROM calificacion_tipo WHERE nombre='Calificacion Normal')
WHERE NOT EXISTS (
  SELECT 1 FROM calificacion c
  WHERE c.calificado_id = (SELECT id FROM usuario WHERE username='luly')
    AND c.autor_id = (SELECT id FROM usuario WHERE username='carol')
);

INSERT INTO calificacion_motivo_calificacion (calificacion_id, motivo_calificacion_id)
SELECT c.id, m.id
FROM calificacion c
JOIN motivo_calificacion m ON m.nombre='Asistencia completa'
WHERE c.descripcion='Excelente administración del evento, todo muy organizado.'
  AND NOT EXISTS (SELECT 1 FROM calificacion_motivo_calificacion cm WHERE cm.calificacion_id=c.id AND cm.motivo_calificacion_id=m.id);


COMMIT;
