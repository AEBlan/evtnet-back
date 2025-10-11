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
INSERT INTO calificacion_tipo (id, nombre, fecha_hora_alta)
VALUES
(1, 'Calificacion Normal', NOW()),
(2, 'Calificacion Denuncia', NOW())
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

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
  'VisionLogUsuariosGrupos','VisionLogEventos','VisionLogEspacios','DenunciaEventos'
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

USE evtnet_db;
START TRANSACTION;

-- =====================================================
-- 0) MINIMOS (por si faltan)
-- =====================================================
-- Tipo de espacio básicos
INSERT INTO tipo_espacio (nombre, descripcion, fecha_hora_alta)
SELECT 'Privado', 'Espacio privado', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tipo_espacio WHERE nombre='Privado');

INSERT INTO tipo_espacio (nombre, descripcion, fecha_hora_alta)
SELECT 'Público', 'Espacio público', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tipo_espacio WHERE nombre='Público');

-- Tipo usuario grupo
INSERT INTO tipo_usuario_grupo (nombre, fecha_hora_alta)
SELECT 'Administrador', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tipo_usuario_grupo WHERE nombre='Administrador');

INSERT INTO tipo_usuario_grupo (nombre, fecha_hora_alta)
SELECT 'Miembro', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tipo_usuario_grupo WHERE nombre='Miembro');

-- Estado de denuncia básico
INSERT INTO estado_denuncia_evento (nombre, descripcion, fecha_hora_alta)
SELECT 'Ingresado','Denuncia creada', NOW()
WHERE NOT EXISTS (SELECT 1 FROM estado_denuncia_evento WHERE nombre='Ingresado');

INSERT INTO estado_denuncia_evento (nombre, descripcion, fecha_hora_alta)
SELECT 'Finalizado','Denuncia cerrada', NOW()
WHERE NOT EXISTS (SELECT 1 FROM estado_denuncia_evento WHERE nombre='Finalizado');

-- Calificación tipo
INSERT INTO calificacion_tipo (nombre, fecha_hora_alta)
SELECT 'Calificacion Normal', NOW()
WHERE NOT EXISTS (SELECT 1 FROM calificacion_tipo WHERE nombre='Calificacion Normal');

-- Medio de pago / comisiones (para comprobantes)
INSERT INTO medio_de_pago (nombre)
SELECT 'Mercado Pago' WHERE NOT EXISTS (SELECT 1 FROM medio_de_pago WHERE nombre='Mercado Pago');

INSERT INTO comision_por_inscripcion (fecha_desde, fecha_hasta, monto_limite, porcentaje)
SELECT NOW(), NULL, 100000.00, 5.00
WHERE NOT EXISTS (SELECT 1 FROM comision_por_inscripcion);

INSERT INTO comision_por_organizacion (fecha_desde, fecha_hasta, monto_limite, porcentaje)
SELECT NOW(), NULL, 250000.00, 7.50
WHERE NOT EXISTS (SELECT 1 FROM comision_por_organizacion);

-- =====================================================
-- 1) USUARIOS BASE (se suman a los 3 que ya tienes)
-- =====================================================
INSERT INTO usuario (nombre, apellido, username, dni, mail, fecha_nacimiento, contrasena, cbu, fecha_hora_alta)
SELECT 'Carolina','Suarez','carol','33333333','carol@example.com','1992-03-10 00:00:00',
       '$2a$10$abcdefghijklmnopqrstuv','CBU0003', NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE username='carol');

INSERT INTO usuario (nombre, apellido, username, dni, mail, fecha_nacimiento, contrasena, cbu, fecha_hora_alta)
SELECT 'Luis','Pérez','luly','44444444','luly@example.com','1994-04-15 00:00:00',
       '$2a$10$abcdefghijklmnopqrstuv','0011223344556677889900', NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE username='luly');

INSERT INTO usuario (nombre, apellido, username, dni, mail, fecha_nacimiento, contrasena, cbu, fecha_hora_alta)
SELECT 'Samuel','Rodriguez','sam','55555555','sam@example.com','1995-05-20 00:00:00',
       '$2a$10$abcdefghijklmnopqrstuv',NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE username='sam');

-- =====================================================
-- 2) ICONOS (según tu entidad: imagen + fecha_hora_alta)
-- =====================================================
INSERT INTO icono_caracteristica (imagen, fecha_hora_alta)
SELECT '/icons/bano.png', NOW()
WHERE NOT EXISTS (SELECT 1 FROM icono_caracteristica WHERE imagen='/icons/bano.png');

INSERT INTO icono_caracteristica (imagen, fecha_hora_alta)
SELECT '/icons/cocina.svg', NOW()
WHERE NOT EXISTS (SELECT 1 FROM icono_caracteristica WHERE imagen='/icons/cocina.svg');

INSERT INTO icono_caracteristica (imagen, fecha_hora_alta)
SELECT '/icons/estacionamiento.svg', NOW()
WHERE NOT EXISTS (SELECT 1 FROM icono_caracteristica WHERE imagen='/icons/estacionamiento.svg');

INSERT INTO icono_caracteristica (imagen, fecha_hora_alta)
SELECT '/icons/wifi.png', NOW()
WHERE NOT EXISTS (SELECT 1 FROM icono_caracteristica WHERE imagen='/icons/wifi.png');

-- =====================================================
-- 3) ESPACIOS (propietarios existentes)
-- =====================================================
INSERT INTO espacio
(nombre, descripcion, fecha_hora_alta, direccion_ubicacion, latitud_ubicacion, longitud_ubicacion, tipo_espacio_id, propietario_id)
SELECT 'Espacio Fantasioso 1','Espacio de prueba para eventos',NOW(),'Avenida Siempreviva 742',-34.603722,-58.381592,
       (SELECT id FROM tipo_espacio WHERE nombre='Privado' LIMIT 1),
       (SELECT id FROM usuario WHERE username='adminevt' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM espacio WHERE nombre='Espacio Fantasioso 1');

INSERT INTO espacio
(nombre, descripcion, fecha_hora_alta, direccion_ubicacion, latitud_ubicacion, longitud_ubicacion, tipo_espacio_id, propietario_id)
SELECT 'Polideportivo Centro','Polideportivo multiuso',NOW(),'Calle Falsa 123',-34.600000,-58.400000,
       (SELECT id FROM tipo_espacio WHERE nombre='Público' LIMIT 1),
       (SELECT id FROM usuario WHERE username='carol' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM espacio WHERE nombre='Polideportivo Centro');

-- =====================================================
-- 4) CARACTERISTICAS (ahora sí, referenciando iconos existentes)
-- =====================================================
INSERT INTO caracteristica (nombre, espacio_id, icono_caracteristica_id)
SELECT 'Baño',
       (SELECT id FROM espacio WHERE nombre='Polideportivo Centro' LIMIT 1),
       (SELECT id FROM icono_caracteristica WHERE imagen='/icons/bano.png' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM caracteristica 
  WHERE nombre='Baño' 
    AND espacio_id=(SELECT id FROM espacio WHERE nombre='Polideportivo Centro' LIMIT 1)
);

INSERT INTO caracteristica (nombre, espacio_id, icono_caracteristica_id)
SELECT 'Estacionamiento',
       (SELECT id FROM espacio WHERE nombre='Polideportivo Centro' LIMIT 1),
       (SELECT id FROM icono_caracteristica WHERE imagen='/icons/estacionamiento.svg' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM caracteristica 
  WHERE nombre='Estacionamiento' 
    AND espacio_id=(SELECT id FROM espacio WHERE nombre='Polideportivo Centro' LIMIT 1)
);

INSERT INTO caracteristica (nombre, espacio_id, icono_caracteristica_id)
SELECT 'Cocina',
       (SELECT id FROM espacio WHERE nombre='Espacio Fantasioso 1' LIMIT 1),
       (SELECT id FROM icono_caracteristica WHERE imagen='/icons/cocina.svg' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM caracteristica 
  WHERE nombre='Cocina' 
    AND espacio_id=(SELECT id FROM espacio WHERE nombre='Espacio Fantasioso 1' LIMIT 1)
);

INSERT INTO caracteristica (nombre, espacio_id, icono_caracteristica_id)
SELECT 'WiFi',
       (SELECT id FROM espacio WHERE nombre='Espacio Fantasioso 1' LIMIT 1),
       (SELECT id FROM icono_caracteristica WHERE imagen='/icons/wifi.png' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM caracteristica 
  WHERE nombre='WiFi' 
    AND espacio_id=(SELECT id FROM espacio WHERE nombre='Espacio Fantasioso 1' LIMIT 1)
);

-- =====================================================
-- 5) SUPEREVENTO + EVENTOS (organizadores existentes)
-- =====================================================
INSERT INTO super_evento (nombre, descripcion, usuario_id)
SELECT 'Liga Primavera','Super torneo de primavera',
       (SELECT id FROM usuario WHERE username='luly' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM super_evento WHERE nombre='Liga Primavera');

-- Evento principal
INSERT INTO evento
(nombre, descripcion, fecha_hora_inicio, fecha_hora_fin, direccion_ubicacion, longitud_ubicacion, latitud_ubicacion,
 precio_inscripcion, cantidad_maxima_invitados, cantidad_maxima_participantes, precio_organizacion,
 super_evento_id, espacio_id, organizador_id, tipo_inscripcion_evento_id, modo_evento_id)
SELECT
 'Fecha 1','Apertura de la liga', NOW() + INTERVAL 1 DAY, NOW() + INTERVAL 1 DAY + INTERVAL 2 HOUR,
 'Calle 123', -58.3816, -34.6037, 100.00, 10, 20, 0.00,
 (SELECT id FROM super_evento WHERE nombre='Liga Primavera' LIMIT 1),
 (SELECT id FROM espacio WHERE nombre='Polideportivo Centro' LIMIT 1),
 (SELECT id FROM usuario WHERE username='luly' LIMIT 1),
 (SELECT id FROM tipo_inscripcion_evento WHERE nombre='Inscripción por Usuario' LIMIT 1),
 (SELECT id FROM modo_evento WHERE nombre='Individual' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM evento WHERE nombre='Fecha 1');

-- =====================================================
-- 6) INSCRIPCION + COMPROBANTE
-- =====================================================
INSERT INTO inscripcion (fecha_hora_alta, precio_inscripcion, permitir_devolucion_completa, usuario_id, evento_id)
SELECT NOW(), 100.00, TRUE,
       (SELECT id FROM usuario WHERE username='sam' LIMIT 1),
       (SELECT id FROM evento WHERE nombre='Fecha 1' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM inscripcion 
  WHERE usuario_id=(SELECT id FROM usuario WHERE username='sam' LIMIT 1)
    AND evento_id=(SELECT id FROM evento WHERE nombre='Fecha 1' LIMIT 1)
);

INSERT INTO comprobante_pago
(numero, concepto, fecha_hora_emision, monto_total_bruto, forma_de_pago, comision,
 inscripcion_id, evento_id, cobro_id, pago_id, medio_de_pago_id, comision_por_inscripcion_id, comision_por_organizacion_id)
SELECT
 'CP-1001','Pago inscripción Fecha 1', NOW(), 100.00, 'Online', 0.00,
 (SELECT id FROM inscripcion WHERE usuario_id=(SELECT id FROM usuario WHERE username='sam') AND evento_id=(SELECT id FROM evento WHERE nombre='Fecha 1')),
 (SELECT id FROM evento WHERE nombre='Fecha 1'),
 (SELECT id FROM usuario WHERE username='luly'),  -- cobra organizador
 (SELECT id FROM usuario WHERE username='sam'),   -- paga participante
 (SELECT id FROM medio_de_pago WHERE nombre='Mercado Pago'),
 (SELECT id FROM comision_por_inscripcion LIMIT 1),
 (SELECT id FROM comision_por_organizacion LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM comprobante_pago WHERE numero='CP-1001');

-- =====================================================
-- 7) DENUNCIA + ESTADOS
-- =====================================================
INSERT INTO denuncia_evento (titulo, descripcion, evento_id, inscripcion_id, denunciante_id)
SELECT 'Juego brusco','Faltas fuertes no sancionadas.',
       (SELECT id FROM evento WHERE nombre='Fecha 1' LIMIT 1),
       NULL,
       (SELECT id FROM usuario WHERE username='sergioalbino' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM denuncia_evento WHERE titulo='Juego brusco');

INSERT INTO denuncia_evento_estado
(descripcion, fecha_hora_desde, fecha_hora_hasta, estado_denuncia_evento_id, denuncia_evento_id, responsable_id)
SELECT 'Creada por sergioalbino', NOW(), NULL,
       (SELECT id FROM estado_denuncia_evento WHERE nombre='Ingresado' LIMIT 1),
       (SELECT id FROM denuncia_evento WHERE titulo='Juego brusco' LIMIT 1),
       NULL
WHERE NOT EXISTS (SELECT 1 FROM denuncia_evento_estado WHERE descripcion='Creada por sergioalbino' AND denuncia_evento_id=(SELECT id FROM denuncia_evento WHERE titulo='Juego brusco' LIMIT 1));

INSERT INTO denuncia_evento_estado
(descripcion, fecha_hora_desde, fecha_hora_hasta, estado_denuncia_evento_id, denuncia_evento_id, responsable_id)
SELECT 'En revisión por admin', NOW(), NULL,
       (SELECT id FROM estado_denuncia_evento WHERE nombre='Finalizado' LIMIT 1),
       (SELECT id FROM denuncia_evento WHERE titulo='Juego brusco' LIMIT 1),
       (SELECT id FROM usuario WHERE username='adminevt' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM denuncia_evento_estado WHERE descripcion='En revisión por admin' AND denuncia_evento_id=(SELECT id FROM denuncia_evento WHERE titulo='Juego brusco' LIMIT 1));

-- =====================================================
-- 8) CALIFICACION (usuario a usuario)
-- =====================================================
INSERT INTO calificacion (descripcion, fecha_hora, calificacion_tipo_id, autor_id, calificado_id)
SELECT 'Muy puntual y colaborador', NOW(),
       (SELECT id FROM calificacion_tipo WHERE nombre='Calificacion Normal' LIMIT 1),
       (SELECT id FROM usuario WHERE username='sergioalbino' LIMIT 1),
       (SELECT id FROM usuario WHERE username='sam' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM calificacion 
  WHERE autor_id=(SELECT id FROM usuario WHERE username='sergioalbino' LIMIT 1)
    AND calificado_id=(SELECT id FROM usuario WHERE username='sam' LIMIT 1)
    AND descripcion='Muy puntual y colaborador'
);

-- =====================================================
-- 9) CHAT DE GRUPO + GRUPO + USUARIOS + MENSAJES
--     (chat tipo ESPACIO asociado a "Polideportivo Centro")
-- =====================================================
INSERT INTO chat (tipo, fecha_hora_alta, espacio_id)
SELECT 'ESPACIO', NOW(), (SELECT id FROM espacio WHERE nombre='Polideportivo Centro' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM chat 
  WHERE tipo='ESPACIO' AND espacio_id=(SELECT id FROM espacio WHERE nombre='Polideportivo Centro' LIMIT 1)
);

SET @chat_grupo := (SELECT id FROM chat WHERE tipo='ESPACIO' AND espacio_id=(SELECT id FROM espacio WHERE nombre='Polideportivo Centro' LIMIT 1) LIMIT 1);

INSERT INTO grupo (nombre, descripcion, chat_id)
SELECT 'Grupo Pádel UTN','Chat grupal para coordinar pádel', @chat_grupo
WHERE NOT EXISTS (SELECT 1 FROM grupo WHERE chat_id=@chat_grupo);

SET @grupo_id := (SELECT id FROM grupo WHERE chat_id=@chat_grupo LIMIT 1);

-- Carol Admin
INSERT INTO usuario_grupo (fecha_hora_alta, usuario_id, grupo_id, tipo_usuario_grupo_id)
SELECT NOW(),
       (SELECT id FROM usuario WHERE username='carol' LIMIT 1),
       @grupo_id,
       (SELECT id FROM tipo_usuario_grupo WHERE nombre='Administrador' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM usuario_grupo 
  WHERE usuario_id=(SELECT id FROM usuario WHERE username='carol' LIMIT 1) AND grupo_id=@grupo_id
);

-- Sam Miembro
INSERT INTO usuario_grupo (fecha_hora_alta, usuario_id, grupo_id, tipo_usuario_grupo_id)
SELECT NOW(),
       (SELECT id FROM usuario WHERE username='sam' LIMIT 1),
       @grupo_id,
       (SELECT id FROM tipo_usuario_grupo WHERE nombre='Miembro' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM usuario_grupo 
  WHERE usuario_id=(SELECT id FROM usuario WHERE username='sam' LIMIT 1) AND grupo_id=@grupo_id
);

-- Mensajes
INSERT INTO mensaje (texto, fecha_hora, chat_id, usuario_id)
SELECT '¿Confirmamos el partido del sábado?', NOW(), @chat_grupo, (SELECT id FROM usuario WHERE username='carol' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM mensaje 
  WHERE chat_id=@chat_grupo AND texto='¿Confirmamos el partido del sábado?'
);

INSERT INTO mensaje (texto, fecha_hora, chat_id, usuario_id)
SELECT '¡Sí, yo voy!', NOW(), @chat_grupo, (SELECT id FROM usuario WHERE username='sam' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM mensaje 
  WHERE chat_id=@chat_grupo AND texto='¡Sí, yo voy!'
);

COMMIT;
START TRANSACTION;

-- ===================================================================
-- USUARIOS (solo referenciamos los ya existentes)
-- ===================================================================
SET @u_sergio := (SELECT id FROM usuario WHERE username='sergioalbino' LIMIT 1);
SET @u_admin  := (SELECT id FROM usuario WHERE username='adminevt'     LIMIT 1);
SET @u_mara   := (SELECT id FROM usuario WHERE username='sergioalbino' LIMIT 1);
SET @u_carol  := (SELECT id FROM usuario WHERE username='sergioalbino' LIMIT 1);
SET @u_luly   := (SELECT id FROM usuario WHERE username='sergioalbino' LIMIT 1);
SET @u_sam    := (SELECT id FROM usuario WHERE username='adminevt'     LIMIT 1);

-- Asegurar que exista el estado "Rechazada"
INSERT INTO estado_denuncia_evento (nombre, descripcion, fecha_hora_alta)
SELECT 'Rechazada', 'Cerrada sin lugar', NOW()
WHERE NOT EXISTS (SELECT 1 FROM estado_denuncia_evento WHERE nombre='Rechazada');

-- Tomar IDs necesarios
SET @est_rech  := (SELECT id FROM estado_denuncia_evento WHERE nombre='Rechazada' LIMIT 1);
SET @den_cobro := (SELECT id FROM denuncia_evento WHERE titulo='Cobro indebido' LIMIT 1);
SET @u_admin   := (SELECT id FROM usuario WHERE username='adminevt' LIMIT 1);

-- Insertar el estado solo si tenemos todo y no existe ya
INSERT INTO denuncia_evento_estado
  (descripcion, fecha_hora_desde, fecha_hora_hasta, estado_denuncia_evento_id, denuncia_evento_id, responsable_id)
SELECT
  'Rechazada por falta de pruebas', NOW(), NULL, @est_rech, @den_cobro, @u_admin
WHERE
  @est_rech IS NOT NULL
  AND @den_cobro IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM denuncia_evento_estado
    WHERE denuncia_evento_id = @den_cobro
      AND estado_denuncia_evento_id = @est_rech
  );
-- ===================================================================
-- NUEVOS ESPACIOS + ADMINISTRADORES DE ESPACIO
-- ===================================================================
INSERT INTO espacio
(nombre, descripcion, fecha_hora_alta, direccion_ubicacion, latitud_ubicacion, longitud_ubicacion, tipo_espacio_id, propietario_id)
SELECT 'Cancha Norte','Cancha techada con césped sintético',NOW(),'Av. Norte 1000',-34.61,-58.39,
       (SELECT id FROM tipo_espacio WHERE nombre='Público' LIMIT 1), @u_sergio
WHERE NOT EXISTS (SELECT 1 FROM espacio WHERE nombre='Cancha Norte');

INSERT INTO espacio
(nombre, descripcion, fecha_hora_alta, direccion_ubicacion, latitud_ubicacion, longitud_ubicacion, tipo_espacio_id, propietario_id)
SELECT 'Salon Multiuso UTN','Salón para eventos sociales y e-sports',NOW(),'Laprida 500',-34.62,-58.38,
       (SELECT id FROM tipo_espacio WHERE nombre='Privado' LIMIT 1), @u_admin
WHERE NOT EXISTS (SELECT 1 FROM espacio WHERE nombre='Salon Multiuso UTN');

-- administradores de espacio (clase intermedia, usa usuario_id)
INSERT INTO administrador_espacio (fecha_hora_alta, espacio_id, usuario_id)
SELECT NOW(), (SELECT id FROM espacio WHERE nombre='Cancha Norte' LIMIT 1), @u_sergio
WHERE NOT EXISTS (
  SELECT 1 FROM administrador_espacio 
  WHERE espacio_id=(SELECT id FROM espacio WHERE nombre='Cancha Norte' LIMIT 1) AND usuario_id=@u_sergio
);

INSERT INTO administrador_espacio (fecha_hora_alta, espacio_id, usuario_id)
SELECT NOW(), (SELECT id FROM espacio WHERE nombre='Salon Multiuso UTN' LIMIT 1), @u_admin
WHERE NOT EXISTS (
  SELECT 1 FROM administrador_espacio 
  WHERE espacio_id=(SELECT id FROM espacio WHERE nombre='Salon Multiuso UTN' LIMIT 1) AND usuario_id=@u_admin
);

-- ===================================================================
-- NUEVO SUPER EVENTO + ADMINISTRADOR DE SUPER EVENTO (intermedia)
-- ===================================================================
INSERT INTO super_evento (nombre, descripcion, usuario_id)
SELECT 'Copa Invierno','Copa invernal multideporte', @u_sergio
WHERE NOT EXISTS (SELECT 1 FROM super_evento WHERE nombre='Copa Invierno');

INSERT INTO administrador_super_evento (fecha_hora_alta, super_evento_id, usuario_id)
SELECT NOW(), (SELECT id FROM super_evento WHERE nombre='Copa Invierno' LIMIT 1), @u_carol
WHERE NOT EXISTS (
  SELECT 1 FROM administrador_super_evento
  WHERE super_evento_id=(SELECT id FROM super_evento WHERE nombre='Copa Invierno' LIMIT 1)
    AND usuario_id=@u_carol
);

-- ===================================================================
-- NUEVOS EVENTOS (no tocamos "Fecha 1" que ya tenías)
-- ===================================================================
INSERT INTO evento
(nombre, descripcion, fecha_hora_inicio, fecha_hora_fin, direccion_ubicacion, longitud_ubicacion, latitud_ubicacion,
 precio_inscripcion, cantidad_maxima_invitados, cantidad_maxima_participantes, precio_organizacion,
 super_evento_id, espacio_id, organizador_id, tipo_inscripcion_evento_id, modo_evento_id)
SELECT
 'Torneo Fútbol 5 - Fecha 2','Segunda fecha del torneo',
 NOW() + INTERVAL 8 DAY, NOW() + INTERVAL 8 DAY + INTERVAL 2 HOUR,
 'Av. Norte 1000', -58.39, -34.61, 150.00, 5, 16, 0.00,
 (SELECT id FROM super_evento WHERE nombre='Liga Primavera' LIMIT 1),
 (SELECT id FROM espacio WHERE nombre='Cancha Norte' LIMIT 1),
 @u_sergio,
 (SELECT id FROM tipo_inscripcion_evento WHERE nombre='Inscripción por Usuario' LIMIT 1),
 (SELECT id FROM modo_evento WHERE nombre='Por equipos' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM evento WHERE nombre='Torneo Fútbol 5 - Fecha 2');

INSERT INTO evento
(nombre, descripcion, fecha_hora_inicio, fecha_hora_fin, direccion_ubicacion, longitud_ubicacion, latitud_ubicacion,
 precio_inscripcion, cantidad_maxima_invitados, cantidad_maxima_participantes, precio_organizacion,
 super_evento_id, espacio_id, organizador_id, tipo_inscripcion_evento_id, modo_evento_id)
SELECT
 'Partido Pádel Nocturno','Encuentro amistoso nocturno',
 NOW() + INTERVAL 3 DAY, NOW() + INTERVAL 3 DAY + INTERVAL 2 HOUR,
 'Calle Falsa 123', -58.40, -34.60, 120.00, 0, 8, 0.00,
 (SELECT id FROM super_evento WHERE nombre='Copa Invierno' LIMIT 1),
 (SELECT id FROM espacio WHERE nombre='Polideportivo Centro' LIMIT 1),
 @u_carol,
 (SELECT id FROM tipo_inscripcion_evento WHERE nombre='Inscripción por Usuario' LIMIT 1),
 (SELECT id FROM modo_evento WHERE nombre='Individual' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM evento WHERE nombre='Partido Pádel Nocturno');

INSERT INTO evento
(nombre, descripcion, fecha_hora_inicio, fecha_hora_fin, direccion_ubicacion, longitud_ubicacion, latitud_ubicacion,
 precio_inscripcion, cantidad_maxima_invitados, cantidad_maxima_participantes, precio_organizacion,
 super_evento_id, espacio_id, organizador_id, tipo_inscripcion_evento_id, modo_evento_id)
SELECT
 'Metegol Relámpago','Mini torneo express de metegol',
 NOW() + INTERVAL 5 DAY, NOW() + INTERVAL 5 DAY + INTERVAL 1 HOUR,
 'Laprida 500', -58.38, -34.62, 80.00, 0, 12, 0.00,
 (SELECT id FROM super_evento WHERE nombre='Copa Invierno' LIMIT 1),
 (SELECT id FROM espacio WHERE nombre='Salon Multiuso UTN' LIMIT 1),
 @u_admin,
 (SELECT id FROM tipo_inscripcion_evento WHERE nombre='Inscripción por Usuario' LIMIT 1),
 (SELECT id FROM modo_evento WHERE nombre='Individual' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM evento WHERE nombre='Metegol Relámpago');

-- ===================================================================
-- DISCIPLINAS PARA LOS NUEVOS EVENTOS
-- ===================================================================
INSERT INTO disciplina_evento (evento_id, disciplina_id)
SELECT (SELECT id FROM evento WHERE nombre='Torneo Fútbol 5 - Fecha 2' LIMIT 1),
       (SELECT id FROM disciplina WHERE nombre='Futbol' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM disciplina_evento 
  WHERE evento_id=(SELECT id FROM evento WHERE nombre='Torneo Fútbol 5 - Fecha 2' LIMIT 1)
    AND disciplina_id=(SELECT id FROM disciplina WHERE nombre='Futbol' LIMIT 1)
);

INSERT INTO disciplina_evento (evento_id, disciplina_id)
SELECT (SELECT id FROM evento WHERE nombre='Metegol Relámpago' LIMIT 1),
       (SELECT id FROM disciplina WHERE nombre='Metegol' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM disciplina_evento 
  WHERE evento_id=(SELECT id FROM evento WHERE nombre='Metegol Relámpago' LIMIT 1)
    AND disciplina_id=(SELECT id FROM disciplina WHERE nombre='Metegol' LIMIT 1)
);

-- ===================================================================
-- ADMINISTRADORES DE EVENTO (intermedia) SOLO PARA LOS NUEVOS
-- ===================================================================
INSERT INTO administrador_evento (fecha_hora_alta, evento_id, usuario_id)
SELECT NOW(), (SELECT id FROM evento WHERE nombre='Torneo Fútbol 5 - Fecha 2' LIMIT 1), @u_admin
WHERE NOT EXISTS (
  SELECT 1 FROM administrador_evento 
  WHERE evento_id=(SELECT id FROM evento WHERE nombre='Torneo Fútbol 5 - Fecha 2' LIMIT 1) AND usuario_id=@u_admin
);

INSERT INTO administrador_evento (fecha_hora_alta, evento_id, usuario_id)
SELECT NOW(), (SELECT id FROM evento WHERE nombre='Partido Pádel Nocturno' LIMIT 1), @u_admin
WHERE NOT EXISTS (
  SELECT 1 FROM administrador_evento 
  WHERE evento_id=(SELECT id FROM evento WHERE nombre='Partido Pádel Nocturno' LIMIT 1) AND usuario_id=@u_admin
);

INSERT INTO administrador_evento (fecha_hora_alta, evento_id, usuario_id)
SELECT NOW(), (SELECT id FROM evento WHERE nombre='Metegol Relámpago' LIMIT 1), @u_admin
WHERE NOT EXISTS (
  SELECT 1 FROM administrador_evento 
  WHERE evento_id=(SELECT id FROM evento WHERE nombre='Metegol Relámpago' LIMIT 1) AND usuario_id=@u_admin
);

-- ===================================================================
-- INSCRIPCIONES + COMPROBANTES (nuevas)
-- ===================================================================
-- mara en Partido Pádel Nocturno
INSERT INTO inscripcion (fecha_hora_alta, precio_inscripcion, permitir_devolucion_completa, usuario_id, evento_id)
SELECT NOW(), 120.00, TRUE, @u_mara, (SELECT id FROM evento WHERE nombre='Partido Pádel Nocturno' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM inscripcion WHERE usuario_id=@u_mara AND evento_id=(SELECT id FROM evento WHERE nombre='Partido Pádel Nocturno' LIMIT 1)
);

INSERT INTO comprobante_pago
(numero, concepto, fecha_hora_emision, monto_total_bruto, forma_de_pago, comision,
 inscripcion_id, evento_id, cobro_id, pago_id, medio_de_pago_id, comision_por_inscripcion_id, comision_por_organizacion_id)
SELECT
 'CP-2001','Pago inscripción Pádel', NOW(), 120.00, 'Online', 0.00,
 (SELECT id FROM inscripcion WHERE usuario_id=@u_mara AND evento_id=(SELECT id FROM evento WHERE nombre='Partido Pádel Nocturno' LIMIT 1)),
 (SELECT id FROM evento WHERE nombre='Partido Pádel Nocturno' LIMIT 1),
 @u_carol, @u_mara,
 (SELECT id FROM medio_de_pago WHERE nombre='Mercado Pago' LIMIT 1),
 (SELECT id FROM comision_por_inscripcion LIMIT 1),
 (SELECT id FROM comision_por_organizacion LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM comprobante_pago WHERE numero='CP-2001');

-- luly en Metegol Relámpago
INSERT INTO inscripcion (fecha_hora_alta, precio_inscripcion, permitir_devolucion_completa, usuario_id, evento_id)
SELECT NOW(), 80.00, TRUE, @u_luly, (SELECT id FROM evento WHERE nombre='Metegol Relámpago' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM inscripcion WHERE usuario_id=@u_luly AND evento_id=(SELECT id FROM evento WHERE nombre='Metegol Relámpago' LIMIT 1)
);

INSERT INTO comprobante_pago
(numero, concepto, fecha_hora_emision, monto_total_bruto, forma_de_pago, comision,
 inscripcion_id, evento_id, cobro_id, pago_id, medio_de_pago_id, comision_por_inscripcion_id, comision_por_organizacion_id)
SELECT
 'CP-3001','Pago inscripción Metegol', NOW(), 80.00, 'Online', 0.00,
 (SELECT id FROM inscripcion WHERE usuario_id=@u_luly AND evento_id=(SELECT id FROM evento WHERE nombre='Metegol Relámpago' LIMIT 1)),
 (SELECT id FROM evento WHERE nombre='Metegol Relámpago' LIMIT 1),
 @u_admin, @u_luly,
 (SELECT id FROM medio_de_pago WHERE nombre='Mercado Pago' LIMIT 1),
 (SELECT id FROM comision_por_inscripcion LIMIT 1),
 (SELECT id FROM comision_por_organizacion LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM comprobante_pago WHERE numero='CP-3001');

-- ===================================================================
-- DENUNCIA NUEVA + ESTADO
-- ===================================================================
INSERT INTO denuncia_evento (titulo, descripcion, evento_id, inscripcion_id, denunciante_id)
SELECT 'Cobro indebido','Se cobró un extra no avisado.',
       (SELECT id FROM evento WHERE nombre='Partido Pádel Nocturno' LIMIT 1),
       (SELECT id FROM inscripcion WHERE usuario_id=@u_mara AND evento_id=(SELECT id FROM evento WHERE nombre='Partido Pádel Nocturno' LIMIT 1)),
       @u_sam
WHERE NOT EXISTS (SELECT 1 FROM denuncia_evento WHERE titulo='Cobro indebido');

INSERT INTO denuncia_evento_estado
(descripcion, fecha_hora_desde, fecha_hora_hasta, estado_denuncia_evento_id, denuncia_evento_id, responsable_id)
SELECT 'Rechazada por falta de pruebas', NOW(), NULL,
       (SELECT id FROM estado_denuncia_evento WHERE nombre='Rechazada' LIMIT 1),
       (SELECT id FROM denuncia_evento WHERE titulo='Cobro indebido' LIMIT 1),
       @u_admin
WHERE NOT EXISTS (
  SELECT 1 FROM denuncia_evento_estado 
  WHERE denuncia_evento_id=(SELECT id FROM denuncia_evento WHERE titulo='Cobro indebido' LIMIT 1)
    AND estado_denuncia_evento_id=(SELECT id FROM estado_denuncia_evento WHERE nombre='Rechazada' LIMIT 1)
);

-- ===================================================================
-- CALIFICACION NUEVA (Normal)
-- ===================================================================
INSERT INTO calificacion (descripcion, fecha_hora, calificacion_tipo_id, autor_id, calificado_id)
SELECT 'Correcto y respetuoso', NOW(),
       (SELECT id FROM calificacion_tipo WHERE nombre='Calificacion Normal' LIMIT 1),
       @u_carol, @u_mara
WHERE NOT EXISTS (
  SELECT 1 FROM calificacion 
  WHERE autor_id=@u_carol AND calificado_id=@u_mara AND descripcion='Correcto y respetuoso'
);

-- ===================================================================
-- CHATS NUEVOS
-- ===================================================================
-- Chat de EVENTO para "Torneo Fútbol 5 - Fecha 2" + un mensaje
INSERT INTO chat (tipo, fecha_hora_alta, evento_id)
SELECT 'EVENTO', NOW(), (SELECT id FROM evento WHERE nombre='Torneo Fútbol 5 - Fecha 2' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM chat WHERE tipo='EVENTO' AND evento_id=(SELECT id FROM evento WHERE nombre='Torneo Fútbol 5 - Fecha 2' LIMIT 1));

INSERT INTO mensaje (texto, fecha_hora, chat_id, usuario_id)
SELECT 'Formaciones listas', NOW(),
       (SELECT id FROM chat WHERE tipo='EVENTO' AND evento_id=(SELECT id FROM evento WHERE nombre='Torneo Fútbol 5 - Fecha 2' LIMIT 1)),
       @u_sergio
WHERE NOT EXISTS (
  SELECT 1 FROM mensaje 
  WHERE chat_id=(SELECT id FROM chat WHERE tipo='EVENTO' AND evento_id=(SELECT id FROM evento WHERE nombre='Torneo Fútbol 5 - Fecha 2' LIMIT 1))
    AND texto='Formaciones listas'
);

-- Chat DIRECTO (único por par) Carol ↔ Sam + mensaje
INSERT INTO chat (tipo, fecha_hora_alta, usuario1_id, usuario2_id)
SELECT 'DIRECTO', NOW(), @u_carol, @u_sam
WHERE NOT EXISTS (SELECT 1 FROM chat WHERE tipo='DIRECTO' AND usuario1_id=@u_carol AND usuario2_id=@u_sam);

INSERT INTO mensaje (texto, fecha_hora, chat_id, usuario_id)
SELECT 'Te paso la ubicación del poli', NOW(),
       (SELECT id FROM chat WHERE tipo='DIRECTO' AND usuario1_id=@u_carol AND usuario2_id=@u_sam LIMIT 1),
       @u_carol
WHERE NOT EXISTS (
  SELECT 1 FROM mensaje 
  WHERE chat_id=(SELECT id FROM chat WHERE tipo='DIRECTO' AND usuario1_id=@u_carol AND usuario2_id=@u_sam LIMIT 1)
    AND texto='Te paso la ubicación del poli'
);


-- ============================================
-- CHATS (primero, porque Grupo depende de Chat)
-- ============================================
INSERT INTO usuario (id, nombre, apellido, username, mail, contrasena, fecha_hora_alta)
SELECT 4, 'Test', 'User', 'testuser', 'test@correo.com', '$2a$10$NlufKRaSkY.5G00DRAxQe.4KlfcfgUze.tGPsBskGwJ4JjPQm43PK', NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE id = 4);

INSERT INTO chat (id, tipo, fecha_hora_alta)
SELECT 200, 'DIRECTO', NOW()
WHERE NOT EXISTS (SELECT 1 FROM chat WHERE id = 200);

INSERT INTO chat (id, tipo, fecha_hora_alta)
SELECT 201, 'DIRECTO', NOW()
WHERE NOT EXISTS (SELECT 1 FROM chat WHERE id = 201);

INSERT INTO chat (id, tipo, fecha_hora_alta)
SELECT 202, 'DIRECTO', NOW()
WHERE NOT EXISTS (SELECT 1 FROM chat WHERE id = 202);

INSERT INTO chat (id, tipo, fecha_hora_alta)
SELECT 203, 'DIRECTO', NOW()
WHERE NOT EXISTS (SELECT 1 FROM chat WHERE id = 203);

INSERT INTO chat (id, tipo, fecha_hora_alta)
SELECT 204, 'DIRECTO', NOW()
WHERE NOT EXISTS (SELECT 1 FROM chat WHERE id = 204);

-- ============================================
-- GRUPOS (cada uno con su chat asociado)
-- ============================================
INSERT INTO grupo (id, nombre, descripcion, chat_id)
SELECT 100, 'Grupo Futbol 5', 'Equipo para jugar los jueves en la noche', 200
WHERE NOT EXISTS (SELECT 1 FROM grupo WHERE id = 100);

INSERT INTO grupo (id, nombre, descripcion, chat_id)
SELECT 101, 'Grupo Tenis', 'Jugamos dobles los fines de semana', 201
WHERE NOT EXISTS (SELECT 1 FROM grupo WHERE id = 101);

INSERT INTO grupo (id, nombre, descripcion, chat_id)
SELECT 102, 'Grupo Ajedrez', 'Amigos que se juntan a practicar ajedrez', 202
WHERE NOT EXISTS (SELECT 1 FROM grupo WHERE id = 102);

INSERT INTO grupo (id, nombre, descripcion, chat_id)
SELECT 103, 'Grupo Running', 'Grupo de running UTN', 203
WHERE NOT EXISTS (SELECT 1 FROM grupo WHERE id = 103);

INSERT INTO grupo (id, nombre, descripcion, chat_id)
SELECT 104, 'Grupo Proyecto UTN', 'Trabajo práctico de ingeniería de software', 204
WHERE NOT EXISTS (SELECT 1 FROM grupo WHERE id = 104);

-- ============================================
-- USUARIO-GRUPO (miembros de los grupos)
-- ============================================

-- Grupo Futbol 5
INSERT INTO usuario_grupo (usuario_id, grupo_id, tipo_usuario_grupo_id, fecha_hora_alta)
SELECT 1, 100, (SELECT id FROM tipo_usuario_grupo WHERE nombre = 'Administrador' LIMIT 1), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario_grupo WHERE usuario_id = 1 AND grupo_id = 100);

INSERT INTO usuario_grupo (usuario_id, grupo_id, tipo_usuario_grupo_id, fecha_hora_alta)
SELECT 2, 100, (SELECT id FROM tipo_usuario_grupo WHERE nombre = 'Miembro' LIMIT 1), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario_grupo WHERE usuario_id = 2 AND grupo_id = 100);

-- Grupo Tenis
INSERT INTO usuario_grupo (usuario_id, grupo_id, tipo_usuario_grupo_id, fecha_hora_alta)
SELECT 2, 101, (SELECT id FROM tipo_usuario_grupo WHERE nombre = 'Administrador' LIMIT 1), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario_grupo WHERE usuario_id = 2 AND grupo_id = 101);

INSERT INTO usuario_grupo (usuario_id, grupo_id, tipo_usuario_grupo_id, fecha_hora_alta)
SELECT 3, 101, (SELECT id FROM tipo_usuario_grupo WHERE nombre = 'Miembro' LIMIT 1), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario_grupo WHERE usuario_id = 3 AND grupo_id = 101);

-- Grupo Ajedrez
INSERT INTO usuario_grupo (usuario_id, grupo_id, tipo_usuario_grupo_id, fecha_hora_alta)
SELECT 4, 102, (SELECT id FROM tipo_usuario_grupo WHERE nombre = 'Administrador' LIMIT 1), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario_grupo WHERE usuario_id = 4 AND grupo_id = 102);

INSERT INTO usuario_grupo (usuario_id, grupo_id, tipo_usuario_grupo_id, fecha_hora_alta)
SELECT 1, 102, (SELECT id FROM tipo_usuario_grupo WHERE nombre = 'Miembro' LIMIT 1), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario_grupo WHERE usuario_id = 1 AND grupo_id = 102);

-- Grupo Running
INSERT INTO usuario_grupo (usuario_id, grupo_id, tipo_usuario_grupo_id, fecha_hora_alta)
SELECT 3, 103, (SELECT id FROM tipo_usuario_grupo WHERE nombre = 'Administrador' LIMIT 1), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario_grupo WHERE usuario_id = 3 AND grupo_id = 103);

INSERT INTO usuario_grupo (usuario_id, grupo_id, tipo_usuario_grupo_id, fecha_hora_alta)
SELECT 1, 103, (SELECT id FROM tipo_usuario_grupo WHERE nombre = 'Miembro' LIMIT 1), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario_grupo WHERE usuario_id = 1 AND grupo_id = 103);

-- Grupo Proyecto UTN
INSERT INTO usuario_grupo (usuario_id, grupo_id, tipo_usuario_grupo_id, fecha_hora_alta)
SELECT 1, 104, (SELECT id FROM tipo_usuario_grupo WHERE nombre = 'Administrador' LIMIT 1), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario_grupo WHERE usuario_id = 1 AND grupo_id = 104);

INSERT INTO usuario_grupo (usuario_id, grupo_id, tipo_usuario_grupo_id, fecha_hora_alta)
SELECT 2, 104, (SELECT id FROM tipo_usuario_grupo WHERE nombre = 'Miembro' LIMIT 1), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario_grupo WHERE usuario_id = 2 AND grupo_id = 104);

INSERT INTO usuario_grupo (usuario_id, grupo_id, tipo_usuario_grupo_id, fecha_hora_alta)
SELECT 3, 104, (SELECT id FROM tipo_usuario_grupo WHERE nombre = 'Miembro' LIMIT 1), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario_grupo WHERE usuario_id = 3 AND grupo_id = 104);

INSERT INTO usuario_grupo (usuario_id, grupo_id, tipo_usuario_grupo_id, fecha_hora_alta)
SELECT 4, 104, (SELECT id FROM tipo_usuario_grupo WHERE nombre = 'Miembro' LIMIT 1), NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuario_grupo WHERE usuario_id = 4 AND grupo_id = 104);

-- ===================================================================
-- VINCULAR USUARIO 'sergioalbino' COMO ADMINISTRADOR DEL GRUPO 1
-- ===================================================================
INSERT INTO usuario_grupo (fecha_hora_alta, usuario_id, grupo_id, tipo_usuario_grupo_id)
SELECT NOW(),
       (SELECT id FROM usuario WHERE username='sergioalbino' LIMIT 1),
       1,
       (SELECT id FROM tipo_usuario_grupo WHERE nombre='Administrador' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM usuario_grupo 
  WHERE usuario_id=(SELECT id FROM usuario WHERE username='sergioalbino' LIMIT 1)
    AND grupo_id=1
);

-- ===================================================================
-- VINCULAR 'carol' COMO MIEMBRO DEL GRUPO 1
-- ===================================================================
INSERT INTO usuario_grupo (fecha_hora_alta, usuario_id, grupo_id, tipo_usuario_grupo_id)
SELECT NOW(),
       (SELECT id FROM usuario WHERE username='carol' LIMIT 1),
       1,
       (SELECT id FROM tipo_usuario_grupo WHERE nombre='Miembro' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM usuario_grupo 
  WHERE usuario_id=(SELECT id FROM usuario WHERE username='carol' LIMIT 1)
    AND grupo_id=1
);

-- ===================================================================
-- VINCULAR 'sam' COMO MIEMBRO DEL GRUPO 1
-- ===================================================================
INSERT INTO usuario_grupo (fecha_hora_alta, usuario_id, grupo_id, tipo_usuario_grupo_id)
SELECT NOW(),
       (SELECT id FROM usuario WHERE username='sam' LIMIT 1),
       1,
       (SELECT id FROM tipo_usuario_grupo WHERE nombre='Miembro' LIMIT 1)
WHERE NOT EXISTS (
  SELECT 1 FROM usuario_grupo 
  WHERE usuario_id=(SELECT id FROM usuario WHERE username='sam' LIMIT 1)
    AND grupo_id=1
);

-- =========================
-- Usuarios (propietario y participantes)
-- =========================
INSERT INTO usuario (id, username, mail, contrasena)
SELECT 10, 'propietario1', 'propietario@test.com', '$2a$10$NlufKRaSkY.5G00DRAxQe.4KlfcfgUze.tGPsBskGwJ4JjPQm43PK'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE id = 10);

INSERT INTO usuario (id, username, mail, contrasena)
SELECT 11, 'userA', 'userA@test.com', '$2a$10$NlufKRaSkY.5G00DRAxQe.4KlfcfgUze.tGPsBskGwJ4JjPQm43PK'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE id = 11);

INSERT INTO usuario (id, username, mail, contrasena)
SELECT 12, 'userB', 'userB@test.com', '$2a$10$NlufKRaSkY.5G00DRAxQe.4KlfcfgUze.tGPsBskGwJ4JjPQm43PK'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE id = 12);

INSERT INTO usuario (id, username, mail, contrasena)
SELECT 74, 'userC', 'userC@test.com', '$2a$10$NlufKRaSkY.5G00DRAxQe.4KlfcfgUze.tGPsBskGwJ4JjPQm43PK'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE id = 74);

-- =========================
-- Roles asignados
-- (asume que rol_id 2=USUARIO, 3=ORGANIZADOR ya existen)
-- =========================
INSERT INTO rol_usuario (id, fecha_hora_alta, rol_id, usuario_id)
SELECT 100, NOW(), 2, 10 WHERE NOT EXISTS (SELECT 1 FROM rol_usuario WHERE id = 100);

INSERT INTO rol_usuario (id, fecha_hora_alta, rol_id, usuario_id)
SELECT 101, NOW(), 3, 10 WHERE NOT EXISTS (SELECT 1 FROM rol_usuario WHERE id = 101);

INSERT INTO rol_usuario (id, fecha_hora_alta, rol_id, usuario_id)
SELECT 102, NOW(), 2, 11 WHERE NOT EXISTS (SELECT 1 FROM rol_usuario WHERE id = 102);

INSERT INTO rol_usuario (id, fecha_hora_alta, rol_id, usuario_id)
SELECT 103, NOW(), 2, 12 WHERE NOT EXISTS (SELECT 1 FROM rol_usuario WHERE id = 103);

INSERT INTO rol_usuario (id, fecha_hora_alta, rol_id, usuario_id)
SELECT 104, NOW(), 2, 74 WHERE NOT EXISTS (SELECT 1 FROM rol_usuario WHERE id = 104);

-- =========================
-- Espacio del propietario
-- =========================
INSERT INTO espacio (id, nombre, descripcion, fecha_hora_alta, direccion_ubicacion, propietario_id)
SELECT 10, 'Polideportivo Central', 'Gimnasio techado', NOW(), 'Av. Siempre Viva 123', 10
WHERE NOT EXISTS (SELECT 1 FROM espacio WHERE id = 10);

-- =========================
-- Eventos dentro del espacio
-- =========================
INSERT INTO evento (id, nombre, descripcion, fecha_hora_inicio, fecha_hora_fin, espacio_id, organizador_id)
SELECT 100, 'Torneo de Ajedrez', 'Competencia abierta', '2025-09-10 18:00:00', '2025-09-10 22:00:00', 10, 10
WHERE NOT EXISTS (SELECT 1 FROM evento WHERE id = 100);

INSERT INTO evento (id, nombre, descripcion, fecha_hora_inicio, fecha_hora_fin, espacio_id, organizador_id)
SELECT 101, 'Clínica de Básquet', 'Entrenamiento especial', '2025-09-15 10:00:00', '2025-09-15 12:00:00', 10, 10
WHERE NOT EXISTS (SELECT 1 FROM evento WHERE id = 101);

INSERT INTO evento (id, nombre, descripcion, fecha_hora_inicio, fecha_hora_fin, espacio_id, organizador_id)
SELECT 102, 'Clase de Yoga', 'Sesión relajante', '2025-09-20 09:00:00', '2025-09-20 10:30:00', 10, 10
WHERE NOT EXISTS (SELECT 1 FROM evento WHERE id = 102);

-- =========================
-- Inscripciones (activas/cancelada)
-- =========================
INSERT INTO inscripcion (id, fecha_hora_alta, precio_inscripcion, permitir_devolucion_completa, usuario_id, evento_id, fecha_baja)
SELECT 1000, NOW(), 100.0, TRUE, 11, 100, NULL
WHERE NOT EXISTS (SELECT 1 FROM inscripcion WHERE id = 1000);

INSERT INTO inscripcion (id, fecha_hora_alta, precio_inscripcion, permitir_devolucion_completa, usuario_id, evento_id, fecha_baja)
SELECT 1001, NOW(), 100.0, TRUE, 12, 100, NULL
WHERE NOT EXISTS (SELECT 1 FROM inscripcion WHERE id = 1001);

INSERT INTO inscripcion (id, fecha_hora_alta, precio_inscripcion, permitir_devolucion_completa, usuario_id, evento_id, fecha_baja)
SELECT 1002, NOW(), 100.0, TRUE, 74, 100, NULL
WHERE NOT EXISTS (SELECT 1 FROM inscripcion WHERE id = 1002);

INSERT INTO inscripcion (id, fecha_hora_alta, precio_inscripcion, permitir_devolucion_completa, usuario_id, evento_id, fecha_baja)
SELECT 1003, NOW(), 200.0, TRUE, 11, 101, NULL
WHERE NOT EXISTS (SELECT 1 FROM inscripcion WHERE id = 1003);

-- Cancelada (no cuenta)
INSERT INTO inscripcion (id, fecha_hora_alta, precio_inscripcion, permitir_devolucion_completa, usuario_id, evento_id, fecha_baja)
SELECT 1004, NOW(), 150.0, TRUE, 12, 101, NOW()
WHERE NOT EXISTS (SELECT 1 FROM inscripcion WHERE id = 1004);

-- Script de seed: Calificaciones de ejemplo (Mara id=3, SergioAlbino id=1)
-- Ejecutar en MariaDB/MySQL

-- 1) CalificacionTipo "Normal"
INSERT INTO calificacion_tipo (id, nombre, fecha_hora_alta)
SELECT 1, 'Calificacion Normal', NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM calificacion_tipo WHERE id = 1 OR nombre = 'Calificacion Normal');

-- 2) TipoCalificacion (emoji/imagen): Buena / Media / Mala
INSERT INTO tipo_calificacion (id, nombre, imagen)
SELECT 1, 'Buena', 'buena.png' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tipo_calificacion WHERE id = 1 OR nombre = 'Buena');

INSERT INTO tipo_calificacion (id, nombre, imagen)
SELECT 2, 'Media', 'media.png' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tipo_calificacion WHERE id = 2 OR nombre = 'Media');

INSERT INTO tipo_calificacion (id, nombre, imagen)
SELECT 3, 'Mala', 'mala.png' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tipo_calificacion WHERE id = 3 OR nombre = 'Mala');

-- 3) MotivoCalificacion (asociados a tipos)
-- (IDs elegidos: 1=Puntual, 2=Asistencia completa, 3=Llegó tarde, 4=No asistió, 5=Se retiró antes)
INSERT INTO motivo_calificacion (id, nombre, tipo_calificacion_id)
SELECT 1, 'Puntual', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM motivo_calificacion WHERE id = 1 OR (nombre='Puntual' AND tipo_calificacion_id=1));

INSERT INTO motivo_calificacion (id, nombre, tipo_calificacion_id)
SELECT 2, 'Asistencia completa', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM motivo_calificacion WHERE id = 2 OR (nombre='Asistencia completa' AND tipo_calificacion_id=1));

INSERT INTO motivo_calificacion (id, nombre, tipo_calificacion_id)
SELECT 3, 'Llegó tarde', 3 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM motivo_calificacion WHERE id = 3 OR (nombre='Llegó tarde' AND tipo_calificacion_id=3));

INSERT INTO motivo_calificacion (id, nombre, tipo_calificacion_id)
SELECT 4, 'No asistió', 3 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM motivo_calificacion WHERE id = 4 OR (nombre='No asistió' AND tipo_calificacion_id=3));

INSERT INTO motivo_calificacion (id, nombre, tipo_calificacion_id)
SELECT 5, 'Se retiró antes', 2 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM motivo_calificacion WHERE id = 5 OR (nombre='Se retiró antes' AND tipo_calificacion_id=2));

-- 4) Insertar la calificacion: autor = SergioAlbino (1), calificado = Mara (3)
-- Usamos id = 500 para la calificacion ejemplo (se salta si ya existe)
INSERT INTO calificacion (id, descripcion, fecha_hora, calificacion_tipo_id, autor_id, calificado_id)
SELECT 500,
       'Calificación por asistencia y puntualidad',
       NOW(),
       1,  -- calificacion_tipo_id = Normal
       1,  -- autor_id = SergioAlbino
       3   -- calificado_id = Mara
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM calificacion WHERE id = 500);

-- 5) Asociar motivos a esa calificacion (calificacion_id = 500)
-- Usamos ids 1001,1002 para las filas intermedias (se saltan si ya existen)
INSERT INTO calificacion_motivo_calificacion (id, calificacion_id, motivo_calificacion_id)
SELECT 1001, 500, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM calificacion_motivo_calificacion WHERE id = 1001 OR (calificacion_id=500 AND motivo_calificacion_id=1));

INSERT INTO calificacion_motivo_calificacion (id, calificacion_id, motivo_calificacion_id)
SELECT 1002, 500, 2 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM calificacion_motivo_calificacion WHERE id = 1002 OR (calificacion_id=500 AND motivo_calificacion_id=2));

-- =============== MOTIVOS POR TIPO ===============
INSERT INTO motivo_calificacion (id, nombre, tipo_calificacion_id)
VALUES
(1, 'Puntual', 1),
(2, 'Respetuoso', 1),
(3, 'Participativo', 1),
(4, 'Regular asistencia', 2),
(5, 'Distracciones', 2),
(6, 'Impuntual', 3),
(7, 'Inasistencia', 3)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);


-- =============== CALIFICACIONES ===============
INSERT INTO calificacion (id, descripcion, fecha_hora, calificacion_tipo_id, autor_id, calificado_id)
VALUES
(100, 'Muy puntual y buena actitud', NOW(), 1, 1, 3),
(101, 'Regular en la asistencia', NOW(), 1, 1, 3),
(102, 'Faltó sin aviso', NOW(), 1, 1, 3),
(103, 'Participó activamente', NOW(), 1, 1, 3)
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

-- =============== CALIFICACION_MOTIVO_CALIFICACION ===============
INSERT INTO calificacion_motivo_calificacion (id, calificacion_id, motivo_calificacion_id)
VALUES
(200, 100, 1), -- Puntual
(201, 100, 2), -- Respetuoso
(202, 101, 4), -- Regular asistencia
(203, 102, 6), -- Impuntual
(204, 102, 7), -- Inasistencia
(205, 103, 3)  -- Participativo
ON DUPLICATE KEY UPDATE calificacion_id = VALUES(calificacion_id);

-- ===================================================================
-- Registros, tipos y subtipos
-- ===================================================================

INSERT INTO registro (nombre, nombre_formateado)
  SELECT "UsuariosGrupos", "Usuarios y Grupos";

INSERT INTO tipo_registro (nombre)
  SELECT "usuario";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "UsuariosGrupos"), (SELECT id FROM tipo_registro WHERE nombre LIKE "usuario");

INSERT INTO tipo_registro (nombre)
  SELECT "grupo";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "UsuariosGrupos"), (SELECT id FROM tipo_registro WHERE nombre LIKE "grupo");

INSERT INTO tipo_registro (nombre)
  SELECT "inicio_sesion";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "UsuariosGrupos"), (SELECT id FROM tipo_registro WHERE nombre LIKE "inicio_sesion");

INSERT INTO tipo_registro (nombre)
  SELECT "calificacion";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "UsuariosGrupos"), (SELECT id FROM tipo_registro WHERE nombre LIKE "calificacion");

INSERT INTO tipo_registro (nombre)
  SELECT "usuario_grupo";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "UsuariosGrupos"), (SELECT id FROM tipo_registro WHERE nombre LIKE "usuario_grupo");




INSERT INTO registro (nombre, nombre_formateado)
  SELECT "Eventos", "Eventos";

INSERT INTO tipo_registro (nombre)
  SELECT "evento";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Eventos"), (SELECT id FROM tipo_registro WHERE nombre LIKE "evento");

INSERT INTO tipo_registro (nombre)
  SELECT "superevento";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Eventos"), (SELECT id FROM tipo_registro WHERE nombre LIKE "superevento");

INSERT INTO tipo_registro (nombre)
  SELECT "inscripcion";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Eventos"), (SELECT id FROM tipo_registro WHERE nombre LIKE "inscripcion");

INSERT INTO tipo_registro (nombre)
  SELECT "denuncia";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Eventos"), (SELECT id FROM tipo_registro WHERE nombre LIKE "denuncia");



INSERT INTO registro (nombre, nombre_formateado)
  SELECT "Espacios", "Espacios";

INSERT INTO tipo_registro (nombre)
  SELECT "espacio_privado";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Espacios"), (SELECT id FROM tipo_registro WHERE nombre LIKE "espacio_privado");

INSERT INTO tipo_registro (nombre)
  SELECT "espacio_publico";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Espacios"), (SELECT id FROM tipo_registro WHERE nombre LIKE "espacio_publico");

INSERT INTO tipo_registro (nombre)
  SELECT "solicitud_espacio_publico";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Espacios"), (SELECT id FROM tipo_registro WHERE nombre LIKE "solicitud_espacio_publico");

INSERT INTO tipo_registro (nombre)
  SELECT "cronograma";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Espacios"), (SELECT id FROM tipo_registro WHERE nombre LIKE "cronograma");

INSERT INTO tipo_registro (nombre)
  SELECT "reseña";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Espacios"), (SELECT id FROM tipo_registro WHERE nombre LIKE "reseña");




INSERT INTO registro (nombre, nombre_formateado)
  SELECT "Pagos", "Pagos";

INSERT INTO tipo_registro (nombre)
  SELECT "pago";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Pagos"), (SELECT id FROM tipo_registro WHERE nombre LIKE "pago");

INSERT INTO tipo_registro (nombre)
  SELECT "devolucion";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Pagos"), (SELECT id FROM tipo_registro WHERE nombre LIKE "devolucion");

INSERT INTO tipo_registro (nombre)
  SELECT "cobro_comision";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Pagos"), (SELECT id FROM tipo_registro WHERE nombre LIKE "cobro_comision");

INSERT INTO tipo_registro (nombre)
  SELECT "pago_comision";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Pagos"), (SELECT id FROM tipo_registro WHERE nombre LIKE "pago_comision");




INSERT INTO registro (nombre, nombre_formateado)
  SELECT "Parametros", "Parámetros";

INSERT INTO tipo_registro (nombre)
  SELECT "tipo_calificacion";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM tipo_registro WHERE nombre LIKE "tipo_calificacion");

INSERT INTO tipo_registro (nombre)
  SELECT "motivo_calificacion";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM tipo_registro WHERE nombre LIKE "motivo_calificacion");

INSERT INTO tipo_registro (nombre)
  SELECT "disciplina";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM tipo_registro WHERE nombre LIKE "disciplina");

INSERT INTO tipo_registro (nombre)
  SELECT "rol";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM tipo_registro WHERE nombre LIKE "rol");

INSERT INTO tipo_registro (nombre)
  SELECT "estado_denuncia";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM tipo_registro WHERE nombre LIKE "estado_denuncia");

INSERT INTO tipo_registro (nombre)
  SELECT "estado_sep";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM tipo_registro WHERE nombre LIKE "estado_sep");

INSERT INTO tipo_registro (nombre)
  SELECT "modo_evento";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM tipo_registro WHERE nombre LIKE "modo_evento");

INSERT INTO tipo_registro (nombre)
  SELECT "icono_caracteristica";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM tipo_registro WHERE nombre LIKE "icono_caracteristica");

INSERT INTO tipo_registro (nombre)
  SELECT "comision_inscripcion";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM tipo_registro WHERE nombre LIKE "comision_inscripcion");

INSERT INTO tipo_registro (nombre)
  SELECT "comision_organizacion";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM tipo_registro WHERE nombre LIKE "comision_organizacion");

INSERT INTO tipo_registro (nombre)
  SELECT "medio_pago";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM tipo_registro WHERE nombre LIKE "medio_pago");

INSERT INTO tipo_registro (nombre)
  SELECT "parametro_sistema";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM tipo_registro WHERE nombre LIKE "parametro_sistema");

INSERT INTO tipo_registro (nombre)
  SELECT "imagen_mascota";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM tipo_registro WHERE nombre LIKE "imagen_mascota");

INSERT INTO tipo_registro (nombre)
  SELECT "instancia_mascota";
INSERT INTO registro_tipo_registro (registro_id, tipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM tipo_registro WHERE nombre LIKE "instancia_mascota");



INSERT INTO subtipo_registro (nombre)
  SELECT "creacion";
INSERT INTO registro_subtipo_registro (registro_id, subtipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "UsuariosGrupos"), (SELECT id FROM subtipo_registro WHERE nombre LIKE "creacion");
INSERT INTO registro_subtipo_registro (registro_id, subtipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Eventos"), (SELECT id FROM subtipo_registro WHERE nombre LIKE "creacion");
INSERT INTO registro_subtipo_registro (registro_id, subtipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Espacios"), (SELECT id FROM subtipo_registro WHERE nombre LIKE "creacion");
INSERT INTO registro_subtipo_registro (registro_id, subtipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM subtipo_registro WHERE nombre LIKE "creacion");


INSERT INTO subtipo_registro (nombre)
  SELECT "eliminacion";
INSERT INTO registro_subtipo_registro (registro_id, subtipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "UsuariosGrupos"), (SELECT id FROM subtipo_registro WHERE nombre LIKE "eliminacion");
INSERT INTO registro_subtipo_registro (registro_id, subtipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Eventos"), (SELECT id FROM subtipo_registro WHERE nombre LIKE "eliminacion");
INSERT INTO registro_subtipo_registro (registro_id, subtipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Espacios"), (SELECT id FROM subtipo_registro WHERE nombre LIKE "eliminacion");
INSERT INTO registro_subtipo_registro (registro_id, subtipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM subtipo_registro WHERE nombre LIKE "eliminacion");


INSERT INTO subtipo_registro (nombre)
  SELECT "modificacion";
INSERT INTO registro_subtipo_registro (registro_id, subtipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "UsuariosGrupos"), (SELECT id FROM subtipo_registro WHERE nombre LIKE "modificacion");
INSERT INTO registro_subtipo_registro (registro_id, subtipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Eventos"), (SELECT id FROM subtipo_registro WHERE nombre LIKE "modificacion");
INSERT INTO registro_subtipo_registro (registro_id, subtipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Espacios"), (SELECT id FROM subtipo_registro WHERE nombre LIKE "modificacion");
INSERT INTO registro_subtipo_registro (registro_id, subtipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Parametros"), (SELECT id FROM subtipo_registro WHERE nombre LIKE "modificacion");


INSERT INTO subtipo_registro (nombre)
  SELECT "ejecucion";
INSERT INTO registro_subtipo_registro (registro_id, subtipo_registro_id)
  SELECT (SELECT id FROM registro WHERE nombre LIKE "Pagos"), (SELECT id FROM subtipo_registro WHERE nombre LIKE "ejecucion");

--Reporte US2

-- 🔹 Espacios
INSERT INTO espacio (id, nombre, descripcion, fecha_hora_alta, direccion_ubicacion, propietario_id)
VALUES
  (20, 'Estadio Norte', 'Cancha techada', NOW(), 'Calle Norte 123', 1),
  (21, 'Gimnasio Sur', 'Sala multiuso', NOW(), 'Av. Sur 456', 1)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- 🔹 Eventos (uno con datos, otro vacío)
INSERT INTO evento (id, nombre, descripcion, fecha_hora_inicio, fecha_hora_fin, espacio_id, organizador_id)
VALUES
  (200, 'Fútbol 5', 'Partido amistoso', '2025-09-01 18:00:00', '2025-09-01 20:00:00', 20, 2),
  (201, 'Vóley', 'Entrenamiento juvenil', '2025-09-05 18:00:00', '2025-09-05 20:00:00', 20, 2),
  (202, 'Crossfit', 'Clase intensa', '2025-09-12 09:00:00', '2025-09-12 10:00:00', 20, 2)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);


COMMIT;