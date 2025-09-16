USE `evtnet_db`;

-- =========================
-- ParametroSistema (sin fecha en tu entidad: lo dejo igual)
-- =========================
INSERT INTO parametro_sistema (nombre, valor)
  SELECT 'longitudPagina', '20' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE nombre='longitudPagina');
INSERT INTO parametro_sistema (nombre, valor)
  SELECT 'eventsMascota', 'load,click,focus,focuslost,enter' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE nombre='eventsMascota');
INSERT INTO parametro_sistema (nombre, valor)
  SELECT 'c_u', '0.4' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE nombre='c_u');
INSERT INTO parametro_sistema (nombre, valor)
  SELECT 'c_d', '0.35' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE nombre='c_d');
INSERT INTO parametro_sistema (nombre, valor)
  SELECT 'c_p', '0.25' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE nombre='c_p');
INSERT INTO parametro_sistema (nombre, valor)
  SELECT 'c_e', '0.3' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE nombre='c_e');
INSERT INTO parametro_sistema (nombre, valor)
  SELECT 'dias_previos_resenas_orden', '365' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE nombre='dias_previos_resenas_orden');

-- =========================
-- MedioDePago (no pegaste entidad; asumo solo nombre)
-- =========================
INSERT INTO medio_de_pago (nombre)
  SELECT 'Mercado Pago' WHERE NOT EXISTS (SELECT 1 FROM medio_de_pago WHERE nombre='Mercado Pago');

-- =========================
-- Comisiones (tienen fecha_desde/fecha_hasta, monto_limite, porcentaje)
-- =========================
INSERT INTO comision_por_inscripcion (fecha_desde, fecha_hasta, monto_limite, porcentaje)
SELECT NOW(), NULL, 100000.00, 5.00
WHERE NOT EXISTS (SELECT 1 FROM comision_por_inscripcion);

INSERT INTO comision_por_organizacion (fecha_desde, fecha_hasta, monto_limite, porcentaje)
SELECT NOW(), NULL, 250000.00, 7.50
WHERE NOT EXISTS (SELECT 1 FROM comision_por_organizacion);

-- =========================
-- Disciplina (tiene fecha_hora_alta/baja opcionales)
-- =========================
INSERT INTO disciplina (nombre, descripcion, fecha_hora_alta)
SELECT 'Futbol', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Futbol');

INSERT INTO disciplina (nombre, descripcion, fecha_hora_alta)
SELECT 'Padel', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Padel');

INSERT INTO disciplina (nombre, descripcion, fecha_hora_alta)
SELECT 'Metegol', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Metegol');

-- =========================
-- ModoEvento (fecha_hora_alta NOT NULL)
-- =========================
INSERT INTO modo_evento (nombre, descripcion, fecha_hora_alta)
SELECT 'Por equipos', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM modo_evento WHERE nombre='Por equipos');

INSERT INTO modo_evento (nombre, descripcion, fecha_hora_alta)
SELECT 'Cooperativo', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM modo_evento WHERE nombre='Cooperativo');

INSERT INTO modo_evento (nombre, descripcion, fecha_hora_alta)
SELECT 'Individual', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM modo_evento WHERE nombre='Individual');

-- =========================
-- TipoInscripcionEvento (fecha_hora_alta NOT NULL)
-- =========================
INSERT INTO tipo_inscripcion_evento (nombre, descripcion, fecha_hora_alta)
SELECT 'Inscripción por Usuario', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM tipo_inscripcion_evento WHERE nombre='Inscripción por Usuario');

INSERT INTO tipo_inscripcion_evento (nombre, descripcion, fecha_hora_alta)
SELECT 'Inscripcion por Administrador', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM tipo_inscripcion_evento WHERE nombre='Inscripcion por Administrador');

INSERT INTO tipo_inscripcion_evento (nombre, descripcion, fecha_hora_alta)
SELECT 'Inscripcion Usuario/Administrador', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM tipo_inscripcion_evento WHERE nombre='Inscripcion Usuario/Administrador');

-- =========================
-- EstadoDenunciaEvento (fecha_hora_alta nullable)
-- =========================
INSERT INTO estado_denuncia_evento (nombre, descripcion, fecha_hora_alta)
SELECT 'Ingresado', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM estado_denuncia_evento WHERE nombre='Ingresado');

INSERT INTO estado_denuncia_evento (nombre, descripcion, fecha_hora_alta)
SELECT 'Finalizado', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM estado_denuncia_evento WHERE nombre='Finalizado');

-- =========================
-- CalificacionTipo (tiene fechas opcionales)
-- =========================
INSERT INTO calificacion_tipo (nombre, fecha_hora_alta)
SELECT 'Calificacion Normal', NOW()
WHERE NOT EXISTS (SELECT 1 FROM calificacion_tipo WHERE nombre='Calificacion Normal');

INSERT INTO calificacion_tipo (nombre, fecha_hora_alta)
SELECT 'Calificacion Denuncia', NOW()
WHERE NOT EXISTS (SELECT 1 FROM calificacion_tipo WHERE nombre='Calificacion Denuncia');

-- =========================
-- TipoUsuarioGrupo (fecha_hora_alta NOT NULL)
-- =========================
INSERT INTO tipo_usuario_grupo (nombre, fecha_hora_alta)
SELECT 'Miembro', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tipo_usuario_grupo WHERE nombre='Miembro');

INSERT INTO tipo_usuario_grupo (nombre, fecha_hora_alta)
SELECT 'Administrador', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tipo_usuario_grupo WHERE nombre='Administrador');

-- =========================
-- EstadoSEP  (¡OJO! tu entidad usa @Table(name = "EstadoSEP"))
-- =========================
INSERT INTO estadosep (nombre)
SELECT 'Pendiente'
WHERE NOT EXISTS (SELECT 1 FROM estadosep WHERE nombre='Pendiente');

INSERT INTO estadosep (nombre)
SELECT 'Aprobada'
WHERE NOT EXISTS (SELECT 1 FROM estadosep WHERE nombre='Aprobada');

INSERT INTO estadosep (nombre)
SELECT 'Rechazada'
WHERE NOT EXISTS (SELECT 1 FROM estadosep WHERE nombre='Rechazada');

-- =========================
-- TipoEspacio (fecha_hora_alta NOT NULL)
-- =========================
INSERT INTO tipo_espacio (nombre, descripcion, fecha_hora_alta)
SELECT 'Privado', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM tipo_espacio WHERE nombre='Privado');

INSERT INTO tipo_espacio (nombre, descripcion, fecha_hora_alta)
SELECT 'Público', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM tipo_espacio WHERE nombre='Público');

-- =========================
-- TipoExcepcionHorarioEspacio (fecha_hora_alta NOT NULL)
-- =========================
INSERT INTO tipo_excepcion_horario_espacio (nombre, fecha_hora_alta)
SELECT 'Completa', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tipo_excepcion_horario_espacio WHERE nombre='Completa');

INSERT INTO tipo_excepcion_horario_espacio (nombre, fecha_hora_alta)
SELECT 'Externa', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tipo_excepcion_horario_espacio WHERE nombre='Externa');

-- =========================
-- Permiso (tu entidad no tiene fecha)
-- =========================
INSERT INTO permiso (nombre) SELECT 'HabilitarCuenta' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='HabilitarCuenta');
INSERT INTO permiso (nombre) SELECT 'InicioSesion' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='InicioSesion');
INSERT INTO permiso (nombre) SELECT 'VisionPerfilPropio' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='VisionPerfilPropio');
INSERT INTO permiso (nombre) SELECT 'ModificacionPerfilPropio' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='ModificacionPerfilPropio');
INSERT INTO permiso (nombre) SELECT 'VisionPerfilTercero' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='VisionPerfilTercero');
INSERT INTO permiso (nombre) SELECT 'VisionPerfilTerceroCompleta' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='VisionPerfilTerceroCompleta');
INSERT INTO permiso (nombre) SELECT 'InscripcionEventos' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='InscripcionEventos');
INSERT INTO permiso (nombre) SELECT 'VisionEventos' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='VisionEventos');
INSERT INTO permiso (nombre) SELECT 'OrganizacionEventos' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='OrganizacionEventos');
INSERT INTO permiso (nombre) SELECT 'AdministracionEventos' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='AdministracionEventos');
INSERT INTO permiso (nombre) SELECT 'VisionEspacios' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='VisionEspacios');
INSERT INTO permiso (nombre) SELECT 'CreacionEspaciosPrivados' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='CreacionEspaciosPrivados');
INSERT INTO permiso (nombre) SELECT 'AdministracionEspaciosPrivados' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='AdministracionEspaciosPrivados');
INSERT INTO permiso (nombre) SELECT 'ParticipacionGrupos' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='ParticipacionGrupos');
INSERT INTO permiso (nombre) SELECT 'CreacionGrupos' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='CreacionGrupos');
INSERT INTO permiso (nombre) SELECT 'AdministracionGrupos' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='AdministracionGrupos');
INSERT INTO permiso (nombre) SELECT 'AdministracionEspaciosPublicos' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='AdministracionEspaciosPublicos');
INSERT INTO permiso (nombre) SELECT 'SolicitudEspaciosPublicos' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='SolicitudEspaciosPublicos');
INSERT INTO permiso (nombre) SELECT 'CalificacionUsuarios' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='CalificacionUsuarios');
INSERT INTO permiso (nombre) SELECT 'DenunciaEventos' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='DenunciaEventos');
INSERT INTO permiso (nombre) SELECT 'ResenaEspacios' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='ResenaEspacios');
INSERT INTO permiso (nombre) SELECT 'AdministracionParametros' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='AdministracionParametros');
INSERT INTO permiso (nombre) SELECT 'AdministracionMascota' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='AdministracionMascota');
INSERT INTO permiso (nombre) SELECT 'AdministracionRoles' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='AdministracionRoles');
INSERT INTO permiso (nombre) SELECT 'AdministracionRolesReservados' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='AdministracionRolesReservados');
INSERT INTO permiso (nombre) SELECT 'AdministracionUsuarios' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='AdministracionUsuarios');
INSERT INTO permiso (nombre) SELECT 'AdministracionUsuariosAdministradores' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='AdministracionUsuariosAdministradores');
INSERT INTO permiso (nombre) SELECT 'VisionReportes' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='VisionReportes');
INSERT INTO permiso (nombre) SELECT 'VisionReportesGenerales' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='VisionReportesGenerales');
INSERT INTO permiso (nombre) SELECT 'RealizacionBackup' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='RealizacionBackup');
INSERT INTO permiso (nombre) SELECT 'VisionLogUsuariosGrupos' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='VisionLogUsuariosGrupos');
INSERT INTO permiso (nombre) SELECT 'VisionLogEventos' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='VisionLogEventos');
INSERT INTO permiso (nombre) SELECT 'VisionLogEspacios' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='VisionLogEspacios');
INSERT INTO permiso (nombre) SELECT 'VisionLogPagos' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='VisionLogPagos');
INSERT INTO permiso (nombre) SELECT 'VisionLogParametros' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='VisionLogParametros');

-- =========================
-- Rol (fecha_hora_alta NOT NULL en tu entidad nueva)
-- =========================
INSERT INTO rol (nombre, descripcion, fecha_hora_alta)
SELECT 'PendienteConfirmación', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre='PendienteConfirmación');

INSERT INTO rol (nombre, descripcion, fecha_hora_alta)
SELECT 'Usuario', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre='Usuario');

INSERT INTO rol (nombre, descripcion, fecha_hora_alta)
SELECT 'Administrador', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre='Administrador');

INSERT INTO rol (nombre, descripcion, fecha_hora_alta)
SELECT 'SuperAdministrador', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre='SuperAdministrador');

INSERT INTO rol (nombre, descripcion, fecha_hora_alta)
SELECT 'Perito', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre='Perito');

-- =========================
-- Rol-Permiso (rol_permiso ahora exige fecha_hora_alta NOT NULL)
-- =========================

-- 1) PendienteConfirmación -> solo HabilitarCuenta
SET @rol := (SELECT id FROM rol WHERE nombre='PendienteConfirmación');
INSERT INTO rol_permiso (rol_id, permiso_id, fecha_hora_alta)
SELECT @rol, p.id, NOW() FROM permiso p
WHERE p.nombre='HabilitarCuenta'
  AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.rol_id=@rol AND rp.permiso_id=p.id
  );

-- 2) Usuario -> permisos básicos
SET @rol := (SELECT id FROM rol WHERE nombre='Usuario');
INSERT INTO rol_permiso (rol_id, permiso_id, fecha_hora_alta)
SELECT @rol, p.id, NOW() FROM permiso p
WHERE p.nombre IN (
  'InicioSesion','VisionPerfilPropio','ModificacionPerfilPropio','VisionPerfilTercero',
  'InscripcionEventos','VisionEventos','OrganizacionEventos','AdministracionEventos',
  'VisionEspacios','CreacionEspaciosPrivados','AdministracionEspaciosPrivados',
  'ParticipacionGrupos','CreacionGrupos','AdministracionGrupos','SolicitudEspaciosPublicos',
  'CalificacionUsuarios','DenunciaEventos','ResenaEspacios','VisionReportes'
)
  AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.rol_id=@rol AND rp.permiso_id=p.id
  );

-- 3) Administrador -> permisos avanzados
SET @rol := (SELECT id FROM rol WHERE nombre='Administrador');
INSERT INTO rol_permiso (rol_id, permiso_id, fecha_hora_alta)
SELECT @rol, p.id, NOW() FROM permiso p
WHERE p.nombre IN (
  'InicioSesion','VisionPerfilPropio','ModificacionPerfilPropio','VisionPerfilTerceroCompleta',
  'VisionEventos','VisionEspacios','ParticipacionGrupos','CreacionGrupos','AdministracionGrupos',
  'AdministracionEspaciosPublicos','SolicitudEspaciosPublicos','AdministracionParametros',
  'AdministracionMascota','AdministracionRoles','AdministracionUsuarios','RealizacionBackup',
  'VisionLogUsuariosGrupos','VisionLogEventos','VisionLogEspacios'
)
  AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.rol_id=@rol AND rp.permiso_id=p.id
  );

-- 4) SuperAdministrador -> permisos globales
SET @rol := (SELECT id FROM rol WHERE nombre='SuperAdministrador');
INSERT INTO rol_permiso (rol_id, permiso_id, fecha_hora_alta)
SELECT @rol, p.id, NOW() FROM permiso p
WHERE p.nombre IN (
  'VisionLogPagos','VisionLogParametros','AdministracionRolesReservados','VisionReportesGenerales'
)
  AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.rol_id=@rol AND rp.permiso_id=p.id
  );

-- 5) Perito -> visión de logs
SET @rol := (SELECT id FROM rol WHERE nombre='Perito');
INSERT INTO rol_permiso (rol_id, permiso_id, fecha_hora_alta)
SELECT @rol, p.id, NOW() FROM permiso p
WHERE p.nombre IN (
  'VisionLogUsuariosGrupos','VisionLogEventos','VisionLogEspacios','VisionLogPagos','VisionLogParametros'
)
  AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.rol_id=@rol AND rp.permiso_id=p.id
  );

-- Saneamos por si había filas antiguas sin fecha
UPDATE rol_permiso SET fecha_hora_alta = NOW() WHERE fecha_hora_alta IS NULL;

 /* =========================================================================
    SEMILLAS PARA PROBAR EVENTOS (respeta nombres de tablas/columnas)
    ========================================================================= */

-- Asegurar fecha_hora_alta en catálogos que la requieren
UPDATE tipo_espacio            SET fecha_hora_alta = COALESCE(fecha_hora_alta, NOW()) WHERE fecha_hora_alta IS NULL;
UPDATE modo_evento             SET fecha_hora_alta = COALESCE(fecha_hora_alta, NOW()) WHERE fecha_hora_alta IS NULL;
UPDATE tipo_inscripcion_evento SET fecha_hora_alta = COALESCE(fecha_hora_alta, NOW()) WHERE fecha_hora_alta IS NULL;

-- =======================
-- Usuarios
-- =======================
INSERT INTO usuario (nombre, apellido, username, dni, mail, fecha_nacimiento, contrasena, cbu, fecha_hora_alta)
SELECT 'Sergio','Albino','sergioalbino','12345678','sergio@example.com','1990-01-01 00:00:00','password','0000000000000000000000', NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE username='sergioalbino');

INSERT INTO usuario (nombre, apellido, username, dni, mail, fecha_nacimiento, contrasena, cbu, fecha_hora_alta)
SELECT 'Admin','Eventos','adminevt','99999999','admin@example.com','1985-05-05 00:00:00','admin','2222222222222222222222', NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE username='adminevt');

SET @u_org   := (SELECT id FROM usuario WHERE username='sergioalbino' LIMIT 1);
SET @u_admin := (SELECT id FROM usuario WHERE username='adminevt'      LIMIT 1);

-- Roles de ejemplo
INSERT INTO rol_usuario (fecha_hora_alta, rol_id, usuario_id)
SELECT NOW(), (SELECT id FROM rol WHERE nombre='Usuario'       LIMIT 1), @u_org
WHERE NOT EXISTS (
  SELECT 1 FROM rol_usuario WHERE usuario_id=@u_org AND rol_id=(SELECT id FROM rol WHERE nombre='Usuario' LIMIT 1)
);

INSERT INTO rol_usuario (fecha_hora_alta, rol_id, usuario_id)
SELECT NOW(), (SELECT id FROM rol WHERE nombre='Administrador' LIMIT 1), @u_admin
WHERE NOT EXISTS (
  SELECT 1 FROM rol_usuario WHERE usuario_id=@u_admin AND rol_id=(SELECT id FROM rol WHERE nombre='Administrador' LIMIT 1)
);

-- =======================
-- Espacios
-- =======================
INSERT INTO espacio
(nombre, descripcion, fecha_hora_alta, direccion_ubicacion, latitud_ubicacion, longitud_ubicacion, tipo_espacio_id, propietario_id)
SELECT 'Espacio Fantasioso 1', 'Espacio de prueba para eventos', NOW(),
       'Avenida Siempreviva 742', -34.603722, -58.381592,
       (SELECT id FROM tipo_espacio WHERE nombre='Privado' LIMIT 1),
       @u_admin
WHERE NOT EXISTS (SELECT 1 FROM espacio WHERE nombre='Espacio Fantasioso 1');

INSERT INTO espacio
(nombre, descripcion, fecha_hora_alta, direccion_ubicacion, latitud_ubicacion, longitud_ubicacion, tipo_espacio_id, propietario_id)
SELECT 'Polideportivo Centro', 'Polideportivo multiuso', NOW(),
       'Calle Falsa 123', -34.60, -58.40,
       (SELECT id FROM tipo_espacio WHERE nombre='Público' LIMIT 1),
       @u_admin
WHERE NOT EXISTS (SELECT 1 FROM espacio WHERE nombre='Polideportivo Centro');

SET @esp1 := (SELECT id FROM espacio WHERE nombre='Espacio Fantasioso 1' LIMIT 1);

-- =======================
-- Evento principal
-- =======================
INSERT INTO evento
(nombre, descripcion, fecha_hora_inicio, fecha_hora_fin,
 direccion_ubicacion, longitud_ubicacion, latitud_ubicacion,
 precio_inscripcion, cantidad_maxima_invitados, cantidad_maxima_participantes, precio_organizacion,
 tipo_inscripcion_evento_id, modo_evento_id, espacio_id, organizador_id)
SELECT
 'Evento Fantástico 1',
 '¡En este evento conocerás muchas personas y formarás amistades para toda tu vida!',
 DATE_ADD(NOW(), INTERVAL 3 DAY),
 DATE_ADD(DATE_ADD(NOW(), INTERVAL 3 DAY), INTERVAL 2 HOUR),
 'Avenida Siempreviva 742', -58.381592, -34.603722,
 2200.00, 2, 10, 10000.00,
 (SELECT id FROM tipo_inscripcion_evento WHERE nombre='Inscripción por Usuario' LIMIT 1),
 (SELECT id FROM modo_evento              WHERE nombre='Individual'              LIMIT 1),
 @esp1, @u_org
WHERE NOT EXISTS (SELECT 1 FROM evento WHERE nombre='Evento Fantástico 1');

SET @ev1 := (SELECT id FROM evento WHERE nombre='Evento Fantástico 1' LIMIT 1);

-- Administrador del evento
INSERT INTO administrador_evento (fecha_hora_alta, usuario_id, evento_id)
SELECT NOW(), @u_admin, @ev1
WHERE NOT EXISTS (SELECT 1 FROM administrador_evento WHERE usuario_id=@u_admin AND evento_id=@ev1);

-- =======================
-- Disciplinas del evento (tabla de cruce DisciplinaEvento)
-- =======================
INSERT INTO disciplina_evento (evento_id, disciplina_id)
SELECT @ev1, (SELECT id FROM disciplina WHERE nombre='Futbol' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM disciplina_evento 
  WHERE evento_id=@ev1 AND disciplina_id=(SELECT id FROM disciplina WHERE nombre='Futbol' LIMIT 1)
);

INSERT INTO disciplina_evento (evento_id, disciplina_id)
SELECT @ev1, (SELECT id FROM disciplina WHERE nombre='Padel' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM disciplina_evento 
  WHERE evento_id=@ev1 AND disciplina_id=(SELECT id FROM disciplina WHERE nombre='Padel' LIMIT 1)
);

-- =======================
-- Inscripción de prueba + invitado
-- =======================
INSERT INTO inscripcion (fecha_hora_alta, precio_inscripcion, permitir_devolucion_completa, usuario_id, evento_id)
SELECT NOW(), 2200.00, TRUE, @u_org, @ev1
WHERE NOT EXISTS (SELECT 1 FROM inscripcion WHERE usuario_id=@u_org AND evento_id=@ev1);

SET @ins1 := (SELECT id FROM inscripcion WHERE usuario_id=@u_org AND evento_id=@ev1 LIMIT 1);

INSERT INTO invitado (nombre, apellido, dni, inscripcion_id)
SELECT 'Tatiana','Duran','87654321', @ins1
WHERE NOT EXISTS (SELECT 1 FROM invitado WHERE dni='87654321' AND inscripcion_id=@ins1);

-- =======================
-- Comprobante de pago de ejemplo
-- =======================
INSERT INTO comprobante_pago
(numero, concepto, fecha_hora_emision, monto_total_bruto, forma_de_pago, comision,
 inscripcion_id, evento_id, cobro_id, pago_id, medio_de_pago_id, comision_por_inscripcion_id, comision_por_organizacion_id)
SELECT
 'CP-0001',
 'Inscripción Evento Fantástico 1',
 NOW(), 2200.00, 'Online', 0.00,
 @ins1, @ev1, @u_admin, @u_org,
 (SELECT id FROM medio_de_pago              WHERE nombre='Mercado Pago' LIMIT 1),
 (SELECT id FROM comision_por_inscripcion   LIMIT 1),
 (SELECT id FROM comision_por_organizacion  LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM comprobante_pago WHERE numero='CP-0001');


-- TipoCalificacion (verde)
INSERT INTO tipo_calificacion (nombre) SELECT 'Buena' WHERE NOT EXISTS (SELECT 1 FROM tipo_calificacion WHERE LOWER(nombre)='buena');
INSERT INTO tipo_calificacion (nombre) SELECT 'Media' WHERE NOT EXISTS (SELECT 1 FROM tipo_calificacion WHERE LOWER(nombre)='media');
INSERT INTO tipo_calificacion (nombre) SELECT 'Mala'  WHERE NOT EXISTS (SELECT 1 FROM tipo_calificacion WHERE LOWER(nombre)='mala');

-- MotivoCalificacion (verde) usando subselects
INSERT INTO motivo_calificacion (nombre, tipo_calificacion_id)
SELECT 'Puntual', (SELECT id FROM tipo_calificacion WHERE LOWER(nombre)='buena')
WHERE NOT EXISTS (
  SELECT 1 FROM motivo_calificacion 
  WHERE LOWER(nombre)='puntual' AND tipo_calificacion_id=(SELECT id FROM tipo_calificacion WHERE LOWER(nombre)='buena')
);

INSERT INTO motivo_calificacion (nombre, tipo_calificacion_id)
SELECT 'Colaborador', (SELECT id FROM tipo_calificacion WHERE LOWER(nombre)='buena')
WHERE NOT EXISTS (
  SELECT 1 FROM motivo_calificacion 
  WHERE LOWER(nombre)='colaborador' AND tipo_calificacion_id=(SELECT id FROM tipo_calificacion WHERE LOWER(nombre)='buena')
);

INSERT INTO motivo_calificacion (nombre, tipo_calificacion_id)
SELECT 'Respetuoso', (SELECT id FROM tipo_calificacion WHERE LOWER(nombre)='buena')
WHERE NOT EXISTS (
  SELECT 1 FROM motivo_calificacion 
  WHERE LOWER(nombre)='respetuoso' AND tipo_calificacion_id=(SELECT id FROM tipo_calificacion WHERE LOWER(nombre)='buena')
);

INSERT INTO motivo_calificacion (nombre, tipo_calificacion_id)
SELECT 'Neutral', (SELECT id FROM tipo_calificacion WHERE LOWER(nombre)='media')
WHERE NOT EXISTS (
  SELECT 1 FROM motivo_calificacion 
  WHERE LOWER(nombre)='neutral' AND tipo_calificacion_id=(SELECT id FROM tipo_calificacion WHERE LOWER(nombre)='media')
);

INSERT INTO motivo_calificacion (nombre, tipo_calificacion_id)
SELECT 'Impuntual', (SELECT id FROM tipo_calificacion WHERE LOWER(nombre)='mala')
WHERE NOT EXISTS (
  SELECT 1 FROM motivo_calificacion 
  WHERE LOWER(nombre)='impuntual' AND tipo_calificacion_id=(SELECT id FROM tipo_calificacion WHERE LOWER(nombre)='mala')
);

INSERT INTO motivo_calificacion (nombre, tipo_calificacion_id)
SELECT 'Incumplidor', (SELECT id FROM tipo_calificacion WHERE LOWER(nombre)='mala')
WHERE NOT EXISTS (
  SELECT 1 FROM motivo_calificacion 
  WHERE LOWER(nombre)='incumplidor' AND tipo_calificacion_id=(SELECT id FROM tipo_calificacion WHERE LOWER(nombre)='mala')
);

INSERT INTO motivo_calificacion (nombre, tipo_calificacion_id)
SELECT 'Grosero', (SELECT id FROM tipo_calificacion WHERE LOWER(nombre)='mala')
WHERE NOT EXISTS (
  SELECT 1 FROM motivo_calificacion 
  WHERE LOWER(nombre)='grosero' AND tipo_calificacion_id=(SELECT id FROM tipo_calificacion WHERE LOWER(nombre)='mala')
);
/*
-- Recomendado: ejecutar en una transacción para poder revertir si algo falla
START TRANSACTION;

-- ====== USUARIOS ======
-- Campos vistos en logs: id (AI), cbu, apellido, contrasena, dni, fecha_baja, fecha_hora_alta, fecha_hora_baja,
-- fecha_nacimiento, foto_perfil, mail, nombre, username

INSERT INTO usuario
(nombre, apellido, username, dni, mail, fecha_nacimiento, foto_perfil, contrasena, CBU,
 fecha_hora_alta, fecha_hora_baja, fecha_baja)
VALUES
('Jose', 'Pérez', 'luly', '12345678', 'sara2608min@gmail.com', '1990-08-26 00:00:00', NULL,
 -- contrasena: usá algo ya codificado en tu sistema; para pruebas puede ser texto si no logueás con esto
 '$2a$10$abcdefghijklmnopqrstuv', '0011223344556677889900',
 NOW(), NULL, NULL),
('sara', 'rodriguez', 'sam', '23456789', 'admin@example.com', '1995-02-10 00:00:00', NULL,
 '$2a$10$abcdefghijklmnopqrstuv', NULL,
 NOW(), NULL, NULL);

-- Guardamos IDs para referencia (si tu cliente permite variables)
-- Si no, obtenelos con un SELECT luego.
SET @id_luly = (SELECT id FROM usuario WHERE username='luly' ORDER BY id DESC LIMIT 1);
SET @id_sam  = (SELECT id FROM usuario WHERE username='sam'  ORDER BY id DESC LIMIT 1);

-- ====== ESPACIOS ======
-- Tabla: espacio
-- Campos clave: nombre, descripcion, fecha_hora_alta, fecha_hora_baja, direccion_ubicacion,
-- latitud_ubicacion, longitud_ubicacion, propietario_id (FK usuario)

INSERT INTO espacio
(nombre, descripcion, fecha_hora_alta, fecha_hora_baja, direccion_ubicacion,
 latitud_ubicacion, longitud_ubicacion, propietario_id)
VALUES
('Cancha Central', 'Cancha techada multiuso', NOW(), NULL, 'Calle 123',
  -34.6037, -58.3816, @id_luly);

SET @id_espacio1 = LAST_INSERT_ID();

-- ====== SUPER EVENTO ======
-- Tabla: super_evento
-- Campos: nombre, descripcion, usuario_id (organizador/owner)
INSERT INTO super_evento
(nombre, descripcion, usuario_id)
VALUES
('Liga Primavera', 'Super torneo de primavera', @id_luly);

SET @id_se1 = LAST_INSERT_ID();

-- ====== EVENTOS ======
-- Tabla: evento
-- Campos vistos: nombre, descripcion, fecha_hora_inicio, fecha_hora_fin, direccion_ubicacion,
-- longitud_ubicacion, latitud_ubicacion, precio_inscripcion, cantidad_maxima_invitados,
-- cantidad_maxima_participantes, precio_organizacion, super_evento_id, espacio_id, organizador_id

INSERT INTO evento
(nombre, descripcion, fecha_hora_inicio, fecha_hora_fin, direccion_ubicacion,
 longitud_ubicacion, latitud_ubicacion, precio_inscripcion, cantidad_maxima_invitados,
 cantidad_maxima_participantes, precio_organizacion, super_evento_id, espacio_id, organizador_id)
VALUES
('Fecha 1', 'Apertura de la liga', NOW() + INTERVAL 1 DAY, NOW() + INTERVAL 1 DAY + INTERVAL 2 HOUR,
 'Calle 123', -58.3816, -34.6037, 100.00, 10, 20, 0.00, @id_se1, @id_espacio1, @id_luly),
('Fecha 2', 'Segunda fecha', NOW() + INTERVAL 8 DAY, NOW() + INTERVAL 8 DAY + INTERVAL 3 HOUR,
 'Calle 123', -58.3816, -34.6037, 120.00, 10, 20, 0.00, @id_se1, @id_espacio1, @id_luly);

-- Guardamos IDs de eventos
SET @id_evento1 = (SELECT id FROM evento WHERE nombre='Fecha 1' ORDER BY id DESC LIMIT 1);
SET @id_evento2 = (SELECT id FROM evento WHERE nombre='Fecha 2' ORDER BY id DESC LIMIT 1);

-- ====== ADMINISTRADORES ======
-- Tabla: administrador_espacio (campos habituales: id AI, fecha_baja, fecha_hora_alta, fecha_hora_baja, espacio_id, usuario_id)
INSERT INTO administrador_espacio (fecha_baja, fecha_hora_alta, fecha_hora_baja, espacio_id, usuario_id)
VALUES (NULL, NOW() - INTERVAL 2 DAY, NULL, @id_espacio1, @id_sam);

-- Tabla: administrador_evento (id AI, fecha_baja, fecha_hora_alta, fecha_hora_baja, evento_id, usuario_id)
INSERT INTO administrador_evento (fecha_baja, fecha_hora_alta, fecha_hora_baja, evento_id, usuario_id)
VALUES (NULL, NOW() - INTERVAL 1 DAY, NULL, @id_evento2, @id_sam);

-- Tabla: administrador_super_evento
-- Por los logs: columnas: id, fecha_baja, fecha_hora_alta, fecha_hora_baja, organizador_id, super_evento_id, usuario_id
INSERT INTO administrador_super_evento
(fecha_baja, fecha_hora_alta, fecha_hora_baja, super_evento_id, usuario_id)
SELECT NULL, NOW() - INTERVAL 3 DAY, NULL, @id_se1, @id_sam
WHERE NOT EXISTS (
  SELECT 1 FROM administrador_super_evento
  WHERE super_evento_id=@id_se1 AND usuario_id=@id_sam
);

-- ====== INSCRIPCIÓN (para que SAM aparezca como participante del Evento 1) ======
-- Ajustá los nombres de columnas de tu tabla de inscripciones si varían.
-- Usualmente: id AI, fecha_hora_alta, fecha_hora_baja, usuario_id, evento_id, etc.
INSERT INTO inscripcion (fecha_hora_alta, fecha_hora_baja,permitir_devolucion_completa, usuario_id, evento_id)
VALUES (NOW() - INTERVAL 12 HOUR, NULL, 1,@id_sam, @id_evento1);

COMMIT;

START TRANSACTION;

-- ===================== CHATS (obligatorios para los grupos) =====================
INSERT INTO chat (id, tipo, fecha_hora_alta, fecha_hora_baja, usuario1_id, usuario2_id)
VALUES
  (600, 'ESPACIO', '2025-06-01 10:00:00', NULL, NULL, NULL),
  (601, 'ESPACIO', '2025-06-15 12:00:00', NULL, NULL, NULL)
ON DUPLICATE KEY UPDATE tipo=VALUES(tipo);

-- ===================== GRUPOS =====================
INSERT INTO grupo (id, nombre, descripcion, chat_id)
VALUES
  (100, 'Grupo de Fútbol', 'Grupo sobre fútbol', 600),
  (101, 'Grupo de Pádel',  'Grupo sobre pádel',  601)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), chat_id=VALUES(chat_id);

-- ===================== TIPOS DE USUARIO-GRUPO =====================
INSERT INTO tipo_usuario_grupo (id, nombre, fecha_hora_alta)
VALUES
  (12, 'Administrador', '2025-01-01 00:00:00'),
  (13, 'Miembro',       '2025-01-01 00:00:00')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- ===================== USUARIO_GRUPO =====================
-- ⚠️ Ajusté para usar los IDs correctos de tipo_usuario_grupo (12 y 13, no 10 y 11)
INSERT INTO usuario_grupo (id, usuario_id, grupo_id, tipo_usuario_grupo_id, fecha_hora_alta)
VALUES
  (1000, 1, 100, 12, '2025-06-01 10:00:00'),  -- luly Admin en Grupo de Fútbol
  (1001, 1, 101, 13, '2025-07-01 15:00:00'),  -- luly Miembro en Grupo de Pádel
  (1002, 2, 100, 13, '2025-06-02 11:00:00')   -- juan Miembro en Grupo de Fútbol
ON DUPLICATE KEY UPDATE tipo_usuario_grupo_id=VALUES(tipo_usuario_grupo_id);

-- ===================== EVENTOS =====================
INSERT INTO evento (id, nombre, descripcion, fecha_hora_inicio, fecha_hora_fin, organizador_id)
VALUES
  (200, 'Torneo Fútbol 5', 'Evento de fútbol', '2025-07-10 20:00:00', '2025-07-10 22:00:00', 1),
  (201, 'Partido Pádel',   'Encuentro de pádel','2025-08-05 19:00:00', '2025-08-05 21:00:00', 1)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- ===================== ESPACIOS =====================
INSERT INTO espacio (id, nombre, descripcion, fecha_hora_alta, fecha_hora_baja, propietario_id)
VALUES
  (300, 'Cancha Techada',        'Cancha indoor',              '2025-06-20 09:00:00', NULL, 1),
  (301, 'Polideportivo Central', 'Polideportivo municipal',    '2025-06-25 09:00:00', NULL, 1)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- ===================== SUPER EVENTO =====================
INSERT INTO super_evento (id, nombre, descripcion, usuario_id)
VALUES
  (400, 'Liga UTN', 'Super evento de deportes universitarios', 1)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

COMMIT;
*/
START TRANSACTION;

-- =====================================================
-- Asegurar ORGANIZADORES en los eventos que ya tenés
-- (ajustá si querés otros)
-- =====================================================
-- Sergio Albino (id=1) organiza Torneo Fútbol 5 (200)
UPDATE evento SET organizador_id = 1 WHERE id = 200;

-- Carol (id=3) organiza Partido Pádel (201)
UPDATE evento SET organizador_id = 3 WHERE id = 201;

-- adminevt (id=2) organiza los demás de prueba (1,10,11)
UPDATE evento SET organizador_id = 2 WHERE id IN (1,10,11);

-- =====================================================
-- ESTADOS de denuncia (si ya existen, se actualizan)
-- =====================================================
INSERT INTO estado_denuncia_evento (id, nombre, descripcion, fecha_hora_alta)
VALUES
  (1, 'Ingresada',   'Denuncia creada por el usuario',        '2025-01-01 00:00:00'),
  (2, 'En revisión', 'Asignada a un responsable para análisis','2025-01-01 00:00:00'),
  (3, 'Resuelta',    'Cerrada con resolución',                 '2025-01-01 00:00:00'),
  (4, 'Rechazada',   'Cerrada sin lugar',                      '2025-01-01 00:00:00')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), descripcion = VALUES(descripcion);

-- =====================================================
-- DENUNCIAS (9000+ para no pisar nada)
-- denunciante_id -> el usuario que denuncia
-- evento_id      -> evento denunciado
-- =====================================================
/*INSERT INTO denuncia_evento (id, titulo, descripcion, evento_id, inscripcion_id, denunciante_id)
VALUES
  (9000, 'Juego brusco',              'Faltas fuertes no sancionadas.',       200, NULL, 11), -- luly denuncia evento 200 (orga: 1)
  (9001, 'Tardanza del organizador',  'Comenzó 30 minutos tarde.',            201, NULL, 12), -- sam  denuncia evento 201 (orga: 3)
  (9002, 'Cobro indebido',            'Se cobró un extra no avisado.',          1, NULL, 13)  -- mara denuncia evento   1 (orga: 2)
ON DUPLICATE KEY UPDATE
  titulo = VALUES(titulo),
  descripcion = VALUES(descripcion),
  evento_id = VALUES(evento_id),
  denunciante_id = VALUES(denunciante_id);

-- =====================================================
-- HISTORIAL / ESTADO ACTUAL de cada denuncia
-- responsable_id = usuario que movió el estado (ej: adminevt id=2)
-- Las fechas hacen que la última fila de cada denuncia quede como estado vigente (fecha_hora_hasta = NULL)
-- =====================================================

-- Denuncia 9000 (luly -> evento 200)
INSERT INTO denuncia_evento_estado
  (id,  descripcion,                     fecha_hora_desde,     fecha_hora_hasta,    estado_denuncia_evento_id, denuncia_evento_id, responsable_id)
VALUES
  (9100,'Creada por luly',               '2025-07-10 22:05:00','2025-07-11 09:00:00', 1,                         9000,               NULL),
  (9101,'En revisión por admin',         '2025-07-11 09:00:00','2025-07-12 12:00:00', 2,                         9000,               2),
  (9102,'Resuelta con advertencia',      '2025-07-12 12:00:00',        NULL,          3,                         9000,               2)
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

-- Denuncia 9001 (sam -> evento 201)
INSERT INTO denuncia_evento_estado
  (id,  descripcion,                     fecha_hora_desde,     fecha_hora_hasta,    estado_denuncia_evento_id, denuncia_evento_id, responsable_id)
VALUES
  (9110,'Creada por sam',                '2025-08-05 21:10:00','2025-08-06 10:00:00', 1,                         9001,               NULL),
  (9111,'En revisión por admin',         '2025-08-06 10:00:00',        NULL,          2,                         9001,               2)
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

-- Denuncia 9002 (mara -> evento 1)
INSERT INTO denuncia_evento_estado
  (id,  descripcion,                     fecha_hora_desde,     fecha_hora_hasta,    estado_denuncia_evento_id, denuncia_evento_id, responsable_id)
VALUES
  (9120,'Creada por mara',               '2025-09-18 03:15:00','2025-09-19 09:00:00', 1,                         9002,               NULL),
  (9121,'Rechazada por falta de pruebas','2025-09-19 09:00:00',        NULL,          4,                         9002,               2)
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

COMMIT;

-- (Opcional) chequeo rápido del “último estado” por denuncia:
-- SELECT de.id, de.titulo, e.nombre AS evento, u1.username AS denunciante,
--        u2.username AS organizador, dee.estado_denuncia_evento_id AS estado_actual, dee.fecha_hora_desde
-- FROM denuncia_evento de
-- JOIN evento e              ON e.id = de.evento_id
-- JOIN usuario u1            ON u1.id = de.denunciante_id
-- JOIN usuario u2            ON u2.id = e.organizador_id
-- JOIN denuncia_evento_estado dee ON dee.denuncia_evento_id = de.id
-- WHERE dee.fecha_hora_hasta IS NULL
-- ORDER BY dee.fecha_hora_desde DESC;
