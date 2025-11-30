USE `evtnet_db`;

-- =========================
-- ParametroSistema (sin fecha en tu entidad: lo dejo igual)
-- =========================
INSERT INTO parametro_sistema (identificador, nombre, descripcion, valor, regex_validacion)
  SELECT 'longitudPagina', 'Longitud de Página', 'Cantidad de resultados a traer en búsquedas con muchos resultados', '20', '^[1-9][0-9]*$' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='longitudPagina');
INSERT INTO parametro_sistema (identificador, nombre, descripcion, valor, regex_validacion)
  SELECT 'c_u', 'Coeficiente de ubicación', 'Coeficiente utilizado para priorizar la cercanía al buscar eventos', '0.4', '^0(\.[0-9]+)?$|^1(\.0+)?$' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='c_u');
INSERT INTO parametro_sistema (identificador, nombre, descripcion, valor, regex_validacion)
  SELECT 'c_d', 'Coeficiente de disciplinas', 'Coeficiente utilizado para priorizar la coincidencia en disciplinas al buscar eventos', '0.35', '^0(\.[0-9]+)?$|^1(\.0+)?$' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='c_d');
INSERT INTO parametro_sistema (identificador, nombre, descripcion, valor, regex_validacion)
  SELECT 'c_p', 'Coeficiente de precio', 'Coeficiente utilizado para priorizar un bajo precio al buscar eventos', '0.25', '^0(\.[0-9]+)?$|^1(\.0+)?$' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='c_p');
INSERT INTO parametro_sistema (identificador, nombre, descripcion, valor, regex_validacion)
  SELECT 'ventana_de_eventos', 'Ventana de eventos', 'Cantidad de eventos pasados a considerar en búsquedas para estimación de ubicación', '20', '^[1-9][0-9]*$' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='ventana_de_eventos');
INSERT INTO parametro_sistema (identificador, nombre, descripcion, valor, regex_validacion)
  SELECT 'cant_max_invitados_default', 'Cantidad máxima por defecto de invitados', 'Cantidad máxima de invitados permitidos por defecto', '5', '^[1-9][0-9]*$' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='cant_max_invitados_default');
INSERT INTO parametro_sistema (identificador, nombre, descripcion, valor, regex_validacion)
  SELECT 'rango_validar_ubicacion', 'Distancia mínima entre espacios propios', 'Distancia mínima en metros que debe haber entre espacios de un mismo usuario', '10', '^[1-9][0-9]*$' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='rango_validar_ubicacion');
INSERT INTO parametro_sistema (identificador, nombre, descripcion, valor, regex_validacion)
  SELECT 'calif_timeout', 'Tiempo hasta próxima calificación', 'Tiempo en horas que debe transcurrir para poder calificar a un mismo usuario nuevamente', '72', '^[1-9][0-9]*$' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='calif_timeout');
INSERT INTO parametro_sistema (identificador, nombre, descripcion, valor, regex_validacion)
  SELECT 'max_d', 'Distancia por defecto para búsqueda', 'Distancia máxima por defecto para búsquedas de eventos', '10000', '^[1-9][0-9]*$' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='max_d');
INSERT INTO parametro_sistema (identificador, nombre, descripcion, valor, regex_validacion)
  SELECT 'max_p', 'Precio por defecto para búsqueda', 'Precio máximo por defecto para búsquedas de eventos', '50000', '^[1-9][0-9]*$' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='max_p');


-- =========================
-- Comisiones (tienen fecha_desde/fecha_hasta, monto_limite, porcentaje)
-- =========================
INSERT INTO comision_por_inscripcion (fecha_desde, fecha_hasta, monto_limite, porcentaje)
SELECT NOW(), NULL, 100000.00, 5.00;
INSERT INTO comision_por_inscripcion (fecha_desde, fecha_hasta, monto_limite, porcentaje)
SELECT NOW(), NULL, 100.00, 1.00;

INSERT INTO comision_por_organizacion (fecha_desde, fecha_hasta, monto_limite, porcentaje)
SELECT NOW(), NULL, 250000.00, 7.50
WHERE NOT EXISTS (SELECT 1 FROM comision_por_organizacion);

-- =========================
-- Disciplina (tiene fecha_hora_alta/baja opcionales)
-- =========================
INSERT INTO disciplina (nombre, descripcion, fecha_hora_alta)
SELECT 'Fútbol', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Fútbol');

INSERT INTO disciplina (nombre, descripcion, fecha_hora_alta)
SELECT 'Fútbol 5', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Fútbol 5');

INSERT INTO disciplina (nombre, descripcion, fecha_hora_alta)
SELECT 'Pádel', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Pádel');

INSERT INTO disciplina (nombre, descripcion, fecha_hora_alta)
SELECT 'Tenis', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Tenis');

INSERT INTO disciplina (nombre, descripcion, fecha_hora_alta)
SELECT 'Tenis de mesa', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Tenis de mesa');

INSERT INTO disciplina (nombre, descripcion, fecha_hora_alta)
SELECT 'Metegol', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Metegol');

INSERT INTO disciplina (nombre, descripcion, fecha_hora_alta)
SELECT 'Ajedrez', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Ajedrez');

INSERT INTO disciplina (nombre, descripcion, fecha_hora_alta)
SELECT 'Patinaje', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Patinaje');

INSERT INTO disciplina (nombre, descripcion, fecha_hora_alta)
SELECT 'Bowling', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Bowling');

INSERT INTO disciplina (nombre, descripcion, fecha_hora_alta)
SELECT 'Pool', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Pool');

INSERT INTO disciplina (nombre, descripcion, fecha_hora_alta)
SELECT 'Basketball', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Basketball');

INSERT INTO disciplina (nombre, descripcion, fecha_hora_alta)
SELECT 'Otro', NULL, NOW() WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Otro');

-- =========================
-- EstadoDenunciaEvento (fecha_hora_alta nullable)
-- =========================
INSERT INTO estado_denuncia_evento (nombre, descripcion, fecha_hora_alta)
SELECT 'Ingresado', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM estado_denuncia_evento WHERE nombre='Ingresado');

INSERT INTO estado_denuncia_evento (nombre, descripcion, fecha_hora_alta)
SELECT 'En Análisis', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM estado_denuncia_evento WHERE nombre='En Análisis');

INSERT INTO estado_denuncia_evento (nombre, descripcion, fecha_hora_alta)
SELECT 'Finalizado', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM estado_denuncia_evento WHERE nombre='Finalizado');

insert into transicion_estado_denuncia (id, fecha_hora_alta, estado_origen_id, estado_destino_id)
select 1, now(), 1,2
WHERE NOT EXISTS (SELECT 1 FROM transicion_estado_denuncia WHERE id=1);

insert into transicion_estado_denuncia (id, fecha_hora_alta, estado_origen_id, estado_destino_id)
select 2, now(), 1,3
WHERE NOT EXISTS (SELECT 1 FROM transicion_estado_denuncia WHERE id=2);

insert into transicion_estado_denuncia (id, fecha_hora_alta, estado_origen_id, estado_destino_id)
select 3, now(), 2,2
WHERE NOT EXISTS (SELECT 1 FROM transicion_estado_denuncia WHERE id=3);

insert into transicion_estado_denuncia (id, fecha_hora_alta, estado_origen_id, estado_destino_id)
select 4, now(), 2,3
WHERE NOT EXISTS (SELECT 1 FROM transicion_estado_denuncia WHERE id=4);


-- =========================
-- CalificacionTipo (tiene fechas opcionales)
-- =========================
INSERT INTO calificacion_tipo (id, nombre, fecha_hora_alta)
VALUES
(1, 'Calificación', NOW()),
(2, 'Denuncia', NOW())
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
-- EstadoSEP  
-- =========================
INSERT INTO estadosep (nombre, fecha_hora_alta)
SELECT 'Pendiente', NOW()
WHERE NOT EXISTS (SELECT 1 FROM estadosep WHERE nombre='Pendiente');

INSERT INTO estadosep (nombre, fecha_hora_alta)
SELECT 'Aprobada', NOW()
WHERE NOT EXISTS (SELECT 1 FROM estadosep WHERE nombre='Aprobada');

INSERT INTO estadosep (nombre, fecha_hora_alta)
SELECT 'Rechazada', NOW()
WHERE NOT EXISTS (SELECT 1 FROM estadosep WHERE nombre='Rechazada');


INSERT INTO transicion_estado_sep (estado_origen_id, estado_destino_id, fecha_hora_alta)
SELECT 1, 2, NOW();

INSERT INTO transicion_estado_sep (estado_origen_id, estado_destino_id, fecha_hora_alta)
SELECT 1, 3, NOW();


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
-- TipoAdministradorEspacio (fecha_hora_alta NOT NULL)
-- =========================
INSERT INTO tipo_administrador_espacio (nombre)
SELECT 'Propietario'
WHERE NOT EXISTS (SELECT 1 FROM tipo_administrador_espacio WHERE nombre='Propietario');

INSERT INTO tipo_administrador_espacio (nombre)
SELECT 'Administrador'
WHERE NOT EXISTS (SELECT 1 FROM tipo_administrador_espacio WHERE nombre='Administrador');

-- =========================
-- TipoExcepcionHorarioEspacio (fecha_hora_alta NOT NULL)
-- =========================
INSERT INTO tipo_excepcion_horario_espacio (nombre, fecha_hora_alta)
SELECT 'Completa', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tipo_excepcion_horario_espacio WHERE nombre='Completa');

INSERT INTO tipo_excepcion_horario_espacio (nombre, fecha_hora_alta)
SELECT 'Externa', NOW()
WHERE NOT EXISTS (SELECT 1 FROM tipo_excepcion_horario_espacio WHERE nombre='Externa');

-- TipoCalificacion (emoji/imagen): Buena / Media / Mala
INSERT INTO tipo_calificacion (id, nombre, imagen, fecha_hora_alta)
SELECT 1, 'Buena', 'buena.png', NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tipo_calificacion WHERE id = 1 OR nombre = 'Buena');

INSERT INTO tipo_calificacion (id, nombre, imagen, fecha_hora_alta)
SELECT 2, 'Media', 'media.png', NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tipo_calificacion WHERE id = 2 OR nombre = 'Media');

INSERT INTO tipo_calificacion (id, nombre, imagen, fecha_hora_alta)
SELECT 3, 'Mala', 'mala.png', NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tipo_calificacion WHERE id = 3 OR nombre = 'Mala');

-- MotivoCalificacion (asociados a tipos)
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

-- =========================
-- TipoAdministradorEvento
-- =========================
INSERT INTO tipo_administrador_evento (nombre)
SELECT "Organizador"
WHERE NOT EXISTS (SELECT 1 FROM tipo_administrador_evento WHERE nombre='Organizador');

INSERT INTO tipo_administrador_evento (nombre)
SELECT "Administrador"
WHERE NOT EXISTS (SELECT 1 FROM tipo_administrador_evento WHERE nombre='Administrador');

-- =========================
-- TipoAdministradorSuperEvento
-- =========================
INSERT INTO tipo_administrador_superevento (nombre)
SELECT "Organizador"
WHERE NOT EXISTS (SELECT 1 FROM tipo_administrador_superevento WHERE nombre='Organizador');

INSERT INTO tipo_administrador_superevento (nombre)
SELECT "Administrador"
WHERE NOT EXISTS (SELECT 1 FROM tipo_administrador_superevento WHERE nombre='Administrador');

-- =========================
-- EstadoEvento
-- =========================
INSERT INTO estado_evento (nombre, descripcion)
SELECT 'En Revisión', 'Evento en estado de revisión'
WHERE NOT EXISTS (SELECT 1 FROM estado_evento WHERE nombre='En Revisión');

INSERT INTO estado_evento (nombre, descripcion)
SELECT 'Aceptado', 'Evento aprobado y visible para los usuarios'
WHERE NOT EXISTS (SELECT 1 FROM estado_evento WHERE nombre='Aceptado');

INSERT INTO estado_evento (nombre, descripcion)
SELECT 'Rechazado', 'Evento rechazado por el administrador del espacio'
WHERE NOT EXISTS (SELECT 1 FROM estado_evento WHERE nombre='Rechazado');

INSERT INTO estado_evento (nombre, descripcion)
SELECT 'Cancelado', 'Evento cancelado por el organizador o administrador'
WHERE NOT EXISTS (SELECT 1 FROM estado_evento WHERE nombre='Cancelado');


-- =========================
-- EventoMascota
-- =========================
INSERT INTO evento_mascota (nombre, valor)
  SELECT 'Click', 'click' WHERE NOT EXISTS (SELECT 1 FROM evento_mascota WHERE valor='click');
INSERT INTO evento_mascota (nombre, valor)
  SELECT 'Foco', 'focus' WHERE NOT EXISTS (SELECT 1 FROM evento_mascota WHERE valor='focus');
INSERT INTO evento_mascota (nombre, valor)
  SELECT 'Pérdida de foco', 'blur' WHERE NOT EXISTS (SELECT 1 FROM evento_mascota WHERE valor='blur');
INSERT INTO evento_mascota (nombre, valor)
  SELECT 'Primera carga', 'load' WHERE NOT EXISTS (SELECT 1 FROM evento_mascota WHERE valor='load');


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
INSERT INTO permiso (nombre) SELECT 'CancelacionEventosAdmin' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='CancelacionEventosAdmin');
INSERT INTO permiso (nombre) SELECT 'AdministracionDisciplinas' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='AdministracionDisciplinas');
INSERT INTO permiso (nombre) SELECT 'AdministracionDenunciasEventos' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='AdministracionDenunciasEventos');
INSERT INTO permiso (nombre) SELECT 'AdministracionSolicitudesEspaciosPrivados' WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre='AdministracionSolicitudesEspaciosPrivados');

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

INSERT INTO rol (nombre, descripcion, fecha_hora_alta)
SELECT 'Gestor de Espacios', NULL, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre='Gestor de Espacios');

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
  'VisionLogUsuariosGrupos','VisionLogEventos','VisionLogEspacios', 'DenunciaEventos',
  'CancelacionEventosAdmin', 'AdministracionDisciplinas', 'AdministracionEspaciosPrivados',
  'AdministracionDenunciasEventos', 'AdministracionSolicitudesEspaciosPrivados'
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

-- 6) Gestor -> visión de solicitudes de espacio
SET @rol := (SELECT id FROM rol WHERE nombre='GestorEspacios');
INSERT INTO rol_permiso (rol_id, permiso_id, fecha_hora_alta)
SELECT @rol, p.id, NOW() FROM permiso p
WHERE p.nombre IN (
  'InicioSesion', 'AdministracionEspaciosPublicos','AdministracionEspaciosPrivados','VisionEspacios','VisionPerfilPropio', 'AdministracionSolicitudesEspaciosPrivados'
)
  AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.rol_id=@rol AND rp.permiso_id=p.id
);

-- Saneamos por si había filas antiguas sin fecha
UPDATE rol_permiso SET fecha_hora_alta = NOW() WHERE fecha_hora_alta IS NULL;

-- =========================
-- Registro: Usuarios y Grupos
-- =========================
INSERT INTO registro (nombre, nombre_formateado)
SELECT 'UsuariosGrupos', 'Usuarios y Grupos'
WHERE NOT EXISTS (SELECT 1 FROM registro WHERE nombre='UsuariosGrupos');

INSERT INTO entidad_registro (nombre)
SELECT 'usuario'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='usuario');

INSERT INTO entidad_registro (nombre)
SELECT 'grupo'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='grupo');

INSERT INTO entidad_registro (nombre)
SELECT 'inicio_sesion'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='inicio_sesion');

INSERT INTO entidad_registro (nombre)
SELECT 'calificacion'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='calificacion');

INSERT INTO entidad_registro (nombre)
SELECT 'usuario_grupo'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='usuario_grupo');


-- =========================
-- Registro: Eventos
-- =========================
INSERT INTO registro (nombre, nombre_formateado)
SELECT 'Eventos', 'Eventos'
WHERE NOT EXISTS (SELECT 1 FROM registro WHERE nombre='Eventos');

INSERT INTO entidad_registro (nombre)
SELECT 'evento'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='evento');

INSERT INTO entidad_registro (nombre)
SELECT 'superevento'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='superevento');

INSERT INTO entidad_registro (nombre)
SELECT 'inscripcion'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='inscripcion');

INSERT INTO entidad_registro (nombre)
SELECT 'denuncia'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='denuncia');

INSERT INTO entidad_registro (nombre)
SELECT 'administrador_evento'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='administrador_evento');

INSERT INTO entidad_registro (nombre)
SELECT 'administrador_superevento'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='administrador_superevento');


-- =========================
-- Registro: Espacios
-- =========================
INSERT INTO registro (nombre, nombre_formateado)
SELECT 'Espacios', 'Espacios'
WHERE NOT EXISTS (SELECT 1 FROM registro WHERE nombre='Espacios');

INSERT INTO entidad_registro (nombre)
SELECT 'espacio_privado'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='espacio_privado');

INSERT INTO entidad_registro (nombre)
SELECT 'espacio_publico'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='espacio_publico');

INSERT INTO entidad_registro (nombre)
SELECT 'subespacio'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='subespacio');

INSERT INTO entidad_registro (nombre)
SELECT 'solicitud_espacio_publico'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='solicitud_espacio_publico');

INSERT INTO entidad_registro (nombre)
SELECT 'cronograma'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='cronograma');

INSERT INTO entidad_registro (nombre)
SELECT 'reseña'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='reseña');

INSERT INTO entidad_registro (nombre)
SELECT 'administrador_espacio_privado'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='administrador_espacio_privado');

INSERT INTO entidad_registro (nombre)
SELECT 'encargado_subespacio'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='encargado_subespacio');


-- =========================
-- Registro: Pagos
-- =========================
INSERT INTO registro (nombre, nombre_formateado)
SELECT 'Pagos', 'Pagos'
WHERE NOT EXISTS (SELECT 1 FROM registro WHERE nombre='Pagos');

INSERT INTO entidad_registro (nombre)
SELECT 'pago'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='pago');

INSERT INTO entidad_registro (nombre)
SELECT 'devolucion'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='devolucion');

INSERT INTO entidad_registro (nombre)
SELECT 'cobro_comision'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='cobro_comision');

INSERT INTO entidad_registro (nombre)
SELECT 'pago_comision'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='pago_comision');


-- =========================
-- Registro: Parámetros
-- =========================
INSERT INTO registro (nombre, nombre_formateado)
SELECT 'Parametros', 'Parámetros'
WHERE NOT EXISTS (SELECT 1 FROM registro WHERE nombre='Parametros');

INSERT INTO entidad_registro (nombre)
SELECT 'tipo_calificacion'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='tipo_calificacion');

INSERT INTO entidad_registro (nombre)
SELECT 'motivo_calificacion'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='motivo_calificacion');

INSERT INTO entidad_registro (nombre)
SELECT 'disciplina'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='disciplina');

INSERT INTO entidad_registro (nombre)
SELECT 'rol'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='rol');

INSERT INTO entidad_registro (nombre)
SELECT 'icono_caracteristica'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='icono_caracteristica');

INSERT INTO entidad_registro (nombre)
SELECT 'comision_inscripcion'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='comision_inscripcion');

INSERT INTO entidad_registro (nombre)
SELECT 'comision_organizacion'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='comision_organizacion');

INSERT INTO entidad_registro (nombre)
SELECT 'parametro_sistema'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='parametro_sistema');

INSERT INTO entidad_registro (nombre)
SELECT 'imagen_mascota'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='imagen_mascota');

INSERT INTO entidad_registro (nombre)
SELECT 'instancia_mascota'
WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='instancia_mascota');

INSERT INTO entidad_registro (nombre)
SELECT 'backup_manual'
    WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='backup_manual');

INSERT INTO entidad_registro (nombre)
SELECT 'backup'
    WHERE NOT EXISTS (SELECT 1 FROM entidad_registro WHERE nombre='backup');


-- =========================
-- Acciones de registro
-- =========================
INSERT INTO accion_registro (nombre)
SELECT 'creacion'
WHERE NOT EXISTS (SELECT 1 FROM accion_registro WHERE nombre='creacion');

INSERT INTO accion_registro (nombre)
SELECT 'eliminacion'
WHERE NOT EXISTS (SELECT 1 FROM accion_registro WHERE nombre='eliminacion');

INSERT INTO accion_registro (nombre)
SELECT 'modificacion'
WHERE NOT EXISTS (SELECT 1 FROM accion_registro WHERE nombre='modificacion');

INSERT INTO accion_registro (nombre)
SELECT 'restauracion'
WHERE NOT EXISTS (SELECT 1 FROM accion_registro WHERE nombre='restauracion');

INSERT INTO accion_registro (nombre)
SELECT 'ejecucion'
WHERE NOT EXISTS (SELECT 1 FROM accion_registro WHERE nombre='ejecucion');

-- =========================
-- EstadoEspacio
-- =========================
INSERT INTO estado_espacio (nombre, descripcion)
SELECT 'En_Revision', 'Se revisan los datos correspondientes al espacio'
WHERE NOT EXISTS (SELECT 1 FROM estado_espacio WHERE nombre='En_Revision');

INSERT INTO estado_espacio (nombre, descripcion)
SELECT 'Rechazado', 'Se rechaza el espacio para que no se puedan organizar eventos'
WHERE NOT EXISTS (SELECT 1 FROM estado_espacio WHERE nombre='Rechazado');

INSERT INTO estado_espacio (nombre, descripcion)
SELECT 'Observado', 'Se observa para que el dueño revise los datos en caso de que alguno esté incorrecto o falte'
WHERE NOT EXISTS (SELECT 1 FROM estado_espacio WHERE nombre='Observado');

INSERT INTO estado_espacio (nombre, descripcion)
SELECT 'Habilitado', 'Se habilita el espacio para que se puedan organizar eventos en él'
WHERE NOT EXISTS (SELECT 1 FROM estado_espacio WHERE nombre='Habilitado');

INSERT INTO estado_espacio (nombre, descripcion)
SELECT 'Oculto', 'Se oculta el espacio para que no se puedan organizar eventos'
WHERE NOT EXISTS (SELECT 1 FROM estado_espacio WHERE nombre='Oculto');

INSERT INTO estado_espacio (nombre, descripcion)
SELECT 'Clausurado', 'Se clausura el espacio debido a irregularidades en el mismo'
WHERE NOT EXISTS (SELECT 1 FROM estado_espacio WHERE nombre='Clausurado');


INSERT INTO transicion_estado_espacio (fecha_hora_alta,fecha_hora_baja,estado_destino_id,estado_origen_id) VALUES
	 ('2025-11-29 16:33:54.000000',NULL,2,1),
	 ('2025-11-29 16:33:54.000000',NULL,3,1),
	 ('2025-11-29 16:33:54.000000',NULL,4,1),
	 ('2025-11-29 16:33:54.000000',NULL,1,3),
	 ('2025-11-29 16:33:54.000000',NULL,5,4),
	 ('2025-11-29 16:33:54.000000',NULL,4,5),
	 ('2025-11-29 16:33:54.000000',NULL,6,4),
	 ('2025-11-29 16:33:54.000000',NULL,6,5);


-- =========================
-- IconoCaracteristica
-- =========================

insert into icono_caracteristica (fecha_hora_alta, imagen)
select NOW(), 'caracteristica1.png';

insert into icono_caracteristica (fecha_hora_alta, imagen)
select NOW(), 'caracteristica2.png';

insert into icono_caracteristica (fecha_hora_alta, imagen)
select NOW(), 'caracteristica3.png';

insert into icono_caracteristica (fecha_hora_alta, imagen)
select NOW(), 'caracteristica4.png';

insert into icono_caracteristica (fecha_hora_alta, imagen)
select NOW(), 'caracteristica5.png';

insert into icono_caracteristica (fecha_hora_alta, imagen)
select NOW(), 'caracteristica6.png';

insert into icono_caracteristica (fecha_hora_alta, imagen)
select NOW(), 'caracteristica7.png';

insert into icono_caracteristica (fecha_hora_alta, imagen)
select NOW(), 'caracteristica8.png';

insert into icono_caracteristica (fecha_hora_alta, imagen)
select NOW(), 'caracteristica9.png';


-- =========================
-- ImagenMascota
-- =========================

insert into imagen_mascota (fecha_hora_alta, imagen, nombre)
select NOW(), 'mascota1.png', 'Pensando';

insert into imagen_mascota (fecha_hora_alta, imagen, nombre)
select NOW(), 'mascota2.png', 'Saludando';

insert into imagen_mascota (fecha_hora_alta, imagen, nombre)
select NOW(), 'mascota3.png', 'Saltando';

insert into imagen_mascota (fecha_hora_alta, imagen, nombre)
select NOW(), 'mascota4.png', 'Detenete';

insert into imagen_mascota (fecha_hora_alta, imagen, nombre)
select NOW(), 'mascota5.png', 'Leyendo';

insert into imagen_mascota (fecha_hora_alta, imagen, nombre)
select NOW(), 'mascota6.png', 'OK';

-- =========================
-- InstanciaMascota
-- =========================

INSERT INTO instancia_mascota (descripcion,fecha_hora_alta,nombre,selector,page_selector) VALUES
	 ('Ayuda para crear o modificar instancias de la mascota','2025-11-15 22:13:07.438','Ayuda Instancia Mascota','button.ayuda','AdministrarInstanciasMascota/*'),
	 ('Explicar el funcionamiento de los subespacios cuando se va a organizar un evento en un espacio','2025-11-16 10:17:08.960','Subespacios al crear evento','button.info_subespacio','CrearEvento/*'),
	 ('Para los usuarios que quieren ayuda a la hora de cargar documentación y bases y condiciones para un espacio privado que están creando','2025-11-19 12:06:15.858','Ayuda Documentación Espacio Privado','button.info_documentacion','CrearEspacio, Espacio/*/Administrar'),
	 ('Ayuda para cuando se agrega un subespacio a un espacio privado o público','2025-11-19 12:12:26.570','Ayuda creación subespacio','button.info_subespacios','CrearEspacio, CrearEspacio/Publico, Espacio/*/Administrar'),
	 ('Para cuando se administra un cronograma en general (no horarios ni excepciones)','2025-11-19 12:32:33.385','Ayuda Cronogramas','button.info_cronograma','Espacio/*/AdministrarCronograma, Espacio/*/AdministrarCronograma/Nuevo, Espacio/*/AdministrarCronograma/*/Modificar'),
	 ('Para los usuarios al administrar los horarios de un cronograma','2025-11-19 12:39:27.667','Ayuda Horarios Cronograma','button.info_horarios','Espacio/*/AdministrarCronograma/*'),
	 ('Para que los propietarios de espacios entiendan cómo usar las excepciones de un cronograma','2025-11-19 12:50:36.693','Ayuda Excepciones Cronograma','button.info_excepciones','Espacio/*/AdministrarCronograma/*/Excepciones, Espacio/*/AdministrarCronograma/*/Excepciones/Nueva');

INSERT INTO instancia_evento_mascota (instancia_mascota_id,evento_mascota_id) VALUES
	 (1,4),
	 (1,1),
	 (2,1),
	 (2,4),
	 (4,1),
	 (3,1),
	 (5,1),
	 (5,4),
	 (6,1),
	 (6,4),
	 (7,1),
	 (7,4);

INSERT INTO instancia_mascota_secuencia (fecha_hora_alta,fecha_hora_baja,orden,texto,imagen_mascota_id,instancia_mascota_id) VALUES
	 ('2025-11-15 22:13:07.556590','2025-11-15 22:20:37.345938',1,'Texto 1',2,1),
	 ('2025-11-15 22:13:07.576826','2025-11-15 22:20:37.345938',2,'Texto 2',3,1),
	 ('2025-11-15 22:20:37.347130','2025-11-15 22:20:50.462662',1,'Texto 1a',2,1),
	 ('2025-11-15 22:20:37.355867','2025-11-15 22:20:50.462662',2,'Texto 2',3,1),
	 ('2025-11-15 22:20:50.463780','2025-11-15 22:30:40.861656',1,'Texto 1',2,1),
	 ('2025-11-15 22:20:50.469037','2025-11-15 22:30:40.861656',2,'Texto 2',3,1),
	 ('2025-11-15 22:30:40.868978','2025-11-16 11:39:37.406368',1,'Texto 1',2,1),
	 ('2025-11-15 22:30:40.929679','2025-11-16 11:39:37.406368',2,'Texto 2',3,1),
	 ('2025-11-15 22:30:40.942519','2025-11-16 11:39:37.406368',3,'Texto 3',6,1),
	 ('2025-11-16 10:17:09.065902','2025-11-16 10:21:02.706086',1,'AAA',3,2),
	 ('2025-11-16 10:17:09.074018','2025-11-16 10:21:02.706086',2,'BBB',1,2),
	 ('2025-11-16 10:17:09.083142','2025-11-16 10:21:02.706086',3,'CCC',4,2),
	 ('2025-11-16 10:21:02.707283','2025-11-16 10:21:07.120438',1,'AAA',3,2),
	 ('2025-11-16 10:21:02.711669','2025-11-16 10:21:07.120438',2,'BBB',1,2),
	 ('2025-11-16 10:21:02.714504','2025-11-16 10:21:07.120438',3,'CCCD',4,2),
	 ('2025-11-16 10:21:07.121516','2025-11-16 10:24:14.974908',1,'AAA',3,2),
	 ('2025-11-16 10:21:07.126106','2025-11-16 10:24:14.974908',2,'BBB',1,2),
	 ('2025-11-16 10:21:07.130739','2025-11-16 10:24:14.974908',3,'CCC',4,2),
	 ('2025-11-16 10:24:14.977999','2025-11-19 11:54:52.225438',1,'AAA',6,2),
	 ('2025-11-16 10:24:14.981813','2025-11-19 11:54:52.225438',2,'BBB',1,2),
	 ('2025-11-16 10:24:14.986368','2025-11-19 11:54:52.225438',3,'CCC',4,2),
	 ('2025-11-16 11:39:37.414952','2025-11-19 10:51:52.934288',1,'Este es el texto de una instancia de la mascota',2,1),
	 ('2025-11-16 11:39:37.638439','2025-11-19 10:51:52.934288',2,'Este es el segundo texto de la secuencia',3,1),
	 ('2025-11-16 11:39:37.643435','2025-11-19 10:51:52.934288',3,'Yyyyy un texto más',6,1),
	 ('2025-11-19 10:51:52.935934','2025-11-19 10:59:31.006034',1,'¡Hola! Yo soy la mascota, Evtnito. Esta es una instancia mía: una secuencia de diapositivas que se muestran al usuario cuando este hace algo dentro de evtnet.',2,1),
	 ('2025-11-19 10:51:52.945442','2025-11-19 10:59:31.006034',2,'Cada instancia tiene un nombre y una descripción. Esto no lo ven los usuarios finales, es solo para que los administradores sepan para qué es la instancia.',5,1),
	 ('2025-11-19 10:51:52.950725','2025-11-19 10:59:31.006034',3,'¿Dónde se muestra una instancia? Eso lo determina el selector de páginas. Solo es cuestión de escribir una lista, separando con comas (,) cada página en la que se podría mostrar la instancia. Se debe poner la dirección a las páginas separando con barras (/), y se puede usar comodines (asteriscos, *). Por ejemplo: "Eventos, Evento/*" (sin las comillas) seleccionaría la página de búsqueda de eventos y las páginas de detalle de cualquier evento.',6,1),
	 ('2025-11-19 10:59:31.013056','2025-11-19 11:37:15.918502',1,'¡Hola! Yo soy la mascota, Evtnito. Esta es una instancia mía: una secuencia de diapositivas que se muestran al usuario cuando este hace algo dentro de evtnet.',2,1),
	 ('2025-11-19 10:59:31.129473','2025-11-19 11:37:15.918502',2,'Cada instancia tiene un nombre y una descripción. Esto no lo ven los usuarios finales, es solo para que los administradores sepan para qué es la instancia.',5,1),
	 ('2025-11-19 10:59:31.135597','2025-11-19 11:37:15.918502',3,'¿Dónde se muestra una instancia? Eso lo determina el selector de páginas. Solo es cuestión de escribir una lista, separando con comas (,) cada página en la que se podría mostrar la instancia. Se debe poner la dirección a las páginas separando con barras (/), y se puede usar comodines (asteriscos, *). Por ejemplo: "Eventos, Evento/*" (sin las comillas) seleccionaría la página de búsqueda de eventos y las páginas de detalle de cualquier evento.',6,1),
	 ('2025-11-19 10:59:31.140766','2025-11-19 11:37:15.918502',4,'Ya habiendo decidido en qué páginas se muestra una instancia, hay que decir cuándo. Para esto están el selector de elementos y los eventos.',5,1),
	 ('2025-11-19 10:59:31.145207','2025-11-19 11:37:15.918502',5,'Por ejemplo, si queremos que se muestre una instancia al hacer click en un botón, se debe escribir un selector CSS que apunte al botón deseado y seleccionar el evento "Click".',3,1),
	 ('2025-11-19 10:59:31.154325','2025-11-19 11:37:15.918502',6,'¿Y si quiero que se muestre una instancia apenas se ingresa a una página? El evento "Carga de página" hace esto. Solo se muestra la primera vez que el usuario va a ver la instancia, ¡si no, sería molesto!',1,1),
	 ('2025-11-19 10:59:31.160211','2025-11-19 11:37:15.918502',7,'Finalmente, la secuencia: son las diapositivas que estás viendo. Cuando se dispara una instancia, se muestra la primera diapositiva. Luego, al ir haciendo click, se pasa a la siguiente, hasta finalizar.',6,1),
	 ('2025-11-19 10:59:31.165677','2025-11-19 11:37:15.918502',8,'Cada diapositiva tiene una imagen a mostrar, acompañada de algo de texto.',5,1),
	 ('2025-11-19 10:59:31.171336','2025-11-19 11:37:15.918502',9,'¡Y listo! ¡Ya sabés como funciona la mascota!',3,1),
	 ('2025-11-19 11:37:15.922525',NULL,1,'¡Hola! Yo soy la mascota, Evtnito. Esta es una instancia mía: una secuencia de diapositivas que se muestran al usuario cuando este hace algo dentro de evtnet.',2,1),
	 ('2025-11-19 11:37:16.094617',NULL,2,'Cada instancia tiene un nombre y una descripción. Esto no lo ven los usuarios finales, es solo para que los administradores sepan para qué es la instancia.',5,1),
	 ('2025-11-19 11:37:16.102554',NULL,3,'¿Dónde se muestra una instancia? Eso lo determina el selector de páginas. Solo es cuestión de escribir una lista, separando con comas (,) cada página en la que se podría mostrar la instancia. Se debe poner la dirección a las páginas separando con barras (/), y se puede usar comodines (asteriscos, *). Por ejemplo: "Eventos, Evento/*" (sin las comillas) seleccionaría la página de búsqueda de eventos y las páginas de detalle de cualquier evento.',6,1),
	 ('2025-11-19 11:37:16.109133',NULL,4,'Ya habiendo decidido en qué páginas se muestra una instancia, hay que decir cuándo. Para esto están el selector de elementos y los eventos.',5,1),
	 ('2025-11-19 11:37:16.117589',NULL,5,'Por ejemplo, si queremos que se muestre una instancia al hacer click en un botón, se debe escribir un selector CSS que apunte al botón deseado y seleccionar el evento "Click".',3,1),
	 ('2025-11-19 11:37:16.124137',NULL,6,'¿Y si quiero que se muestre una instancia apenas se ingresa a una página? El evento "Carga de página" hace esto. Solo se muestra la primera vez que el usuario va a ver la instancia, ¡si no, sería molesto!',1,1),
	 ('2025-11-19 11:37:16.130788',NULL,7,'Finalmente, la secuencia: son las diapositivas que estás viendo. Cuando se dispara una instancia, se muestra la primera diapositiva. Luego, al ir haciendo click, se pasa a la siguiente, hasta finalizar.',6,1),
	 ('2025-11-19 11:37:16.136616',NULL,8,'Cada diapositiva tiene una imagen a mostrar, acompañada de algo de texto. Se puede agregar una diapositiva con el botón "Más" (+), y quitarla con la cruz (x). También se puede previsualizar la secuencia con el botón del ojo.',5,1),
	 ('2025-11-19 11:37:16.141793',NULL,9,'¡Y listo! ¡Ya sabés como funciona la mascota!',3,1),
	 ('2025-11-19 11:54:52.233628',NULL,1,'Siempre que vas a organizar un evento, tenés que seleccionar el subespacio, es decir el salón o cancha donde se va a hacer dentro de un espacio.',2,2),
	 ('2025-11-19 11:54:52.343777',NULL,2,'Si el subespacio tiene horarios disponibles, es necesario que selecciones uno. Primero debés seleccionar la fecha, presionar el botón "Buscar" y seleccionar el horario que desees en esa fecha, si hubiera alguno disponible.',5,2),
	 ('2025-11-19 11:54:52.347572',NULL,3,' Luego debés seleccionar la(s) disciplina(s) que vayan a desarrollar en tu evento.',6,2),
	 ('2025-11-19 11:54:52.351570',NULL,4,'El horario que selecciones puede implicar, en espacios privados, una cuota por organización (pagada por el organizador) y un adicional por inscripción (pagada por cada inscripto). El precio de inscripción que coloques será la base a la que se le sumará el adicional por inscripción. Además, evtnet puede cobrar una comisión que también se sumará al precio de inscripción. Podés ver el monto final que pagarán los inscriptos debajo del monto que cobrás vos por cada inscripción',1,2),
	 ('2025-11-19 11:54:52.355913',NULL,5,'Por último, hay que establecer cuántos participantes puede haber en el evento, y leer y aceptar las bases y condiciones del espacio.',6,2),
	 ('2025-11-19 11:54:52.360524',NULL,6,'¡Ya podés organizar tu evento!',3,2),
	 ('2025-11-19 12:06:15.868464',NULL,1,'¿Qué documentación debería cargar para registrar mi espacio?',1,3),
	 ('2025-11-19 12:06:15.875901',NULL,2,'Primero, debés identificarte. Podrías cargar una foto de tu DNI o pasaporte.',2,3),
	 ('2025-11-19 12:06:15.879960',NULL,3,'También tenemos que verificar que el espacio es tuyo: podrías cargar un comprobante de pago de algún servicio, la escritura o boleto de compraventa, por ejemplo.',5,3),
	 ('2025-11-19 12:06:15.883959',NULL,4,'Si estás alquilando el espacio, necesitamos el contrato de locación.',5,3),
	 ('2025-11-19 12:06:15.890102',NULL,5,'¡Con eso es suficiente! SI hay algún problema, serás notificado.',6,3),
	 ('2025-11-19 12:06:15.894196',NULL,6,'Recordá que también podés cargar las bases y condiciones para aquellos que organicen eventos en el espacio.',2,3),
	 ('2025-11-19 12:12:26.576972',NULL,1,'Cada espacio cuenta con al menos un subespacio: un salón, cancha o lugar específico donde se pueden organizar eventos.',2,4),
	 ('2025-11-19 12:12:26.584471',NULL,2,'Cada subespacio tiene su nombre, descripción, capacidad máxima y disciplinas soportadas.',6,4),
	 ('2025-11-19 12:12:26.591128',NULL,3,'Además, cada subespacio tiene su propio cronograma. Una vez habilitado el espacio y sus subespacios, se puede gestionar su cronograma en la página de administración del espacio.',5,4),
	 ('2025-11-19 12:12:26.599057',NULL,4,'No te preocupes si no querés registrar todos los subespacios ahora, más adelante podrás agregar otros.',2,4),
	 ('2025-11-19 12:32:33.390574',NULL,1,'Los subespacios usan cronogramas para definir los horarios (turnos) en los que otras personas pueden organizar eventos.',2,5),
	 ('2025-11-19 12:32:33.395686',NULL,2,'Los cronogramas tienen un periodo de vigencia, y solo puede haber uno vigente a la vez por subespacio.',5,5),
	 ('2025-11-19 12:32:33.399113',NULL,3,'Asegurate de que haya algún cronograma vigente, ¡o personas externas a tu espacio no podrán organizar eventos!',4,5),
	 ('2025-11-19 12:32:33.401855',NULL,4,'También es necesario que establezcas la anticipación máxima de reserva. Esta es la cantidad de días de antelación con la que los usuarios podrán organizar un evento en el subespacio. Por ejemplo, si la establecés en 10, no podrán organizarse eventos 15 días antes de la fecha deseada, pero sí 8.',5,5),
	 ('2025-11-19 12:32:33.404029',NULL,5,'¡Recordá establecer los horarios de los cronogramas!',2,5),
	 ('2025-11-19 12:39:27.675367',NULL,1,'Cada cronograma tiene horarios: esto es turnos en los que se pueden organizar eventos. Estos horarios se repiten semanalmente.',5,6),
	 ('2025-11-19 12:39:27.680887',NULL,2,'Cada horario tiene, además, dos precios, que serán la forma en que el espacio genera ingresos por medio de evtnet.',1,6),
	 ('2025-11-19 12:39:27.687824',NULL,3,'El precio para organizar eventos lo pagan el organizador de un evento al registrarlo en el espacio,',5,6),
	 ('2025-11-19 12:39:27.694314',NULL,4,'y el precio adicional por insciripción lo paga cada usuario al inscribirse a un evento en el subespacio.',6,6),
	 ('2025-11-19 12:50:36.702864','2025-11-28 17:42:28.424768',1,'Las excepciones son un periodo de tiempo dentro de un cronograma en el que no se podrá organizar eventos.',5,7),
	 ('2025-11-19 12:50:36.719238','2025-11-28 17:42:28.424768',2,'Podrías querer usar una excepción, por ejemplo, por vacaciones, o para hacer reparaciones en un subespacio.',6,7),
	 ('2025-11-19 12:50:36.724122','2025-11-28 17:42:28.424768',3,'Recordá que si nadie debería poder hacer un evento, el tipo de excepción es "Completa", pero si los administradores sí deberían ser capaces, es "Externa".',2,7),
	 ('2025-11-28 17:42:28.427196',NULL,1,'Las excepciones son un periodo de tiempo dentro de un cronograma en el que no se podrá organizar eventos.',6,7),
	 ('2025-11-28 17:42:28.479264',NULL,2,'Recordá que si nadie debería poder hacer un evento, el tipo de excepción es "Completa", pero si los administradores sí deberían ser capaces, es "Externa".',2,7),
	 ('2025-11-28 17:42:28.483638',NULL,3,'sda',5,7);

-- =========================
-- Usuario
-- =========================

INSERT INTO usuario (cbu,apellido,contrasena,dni,fecha_hora_alta,fecha_hora_baja,fecha_nacimiento,foto_perfil,mail,nombre,username,mercado_pago_access_token,mercado_pago_public_key,mercado_pago_refresh_token,mercado_pago_user_id) VALUES
	 (NULL,'Blan','$2a$10$90iDGpqk8xjyhXc.BX7tbOz6dJDZy/t5.FlYUy0fuEbbGQwFFcLYO','34273429','2025-11-06 17:18:33.215337',NULL,'1998-01-08 00:00:00.000000',NULL,'andresblan@maildrop.cc','Andrés','andresblan',NULL,NULL,NULL,NULL),
	 (NULL,'Albino','$2a$10$4lXc3XNX2gZCzb1ScxjdJ.b4tjfVKhlwUY9IO3dxSx2NlVF0eoxGu','42832984','2025-11-06 17:24:32.919015',NULL,'2002-05-08 00:00:00.000000',NULL,'sergioalbino@maildrop.cc','Sergio','sergioalbino','APP_USR-401897791915576-112816-24eea7daa04f89b2af8e5fc1e26be1ff-2918163140','APP_USR-b58d12a8-eb62-41cb-b752-8675af913af3','TG-692a05245240fe0001b72584-2918163140',2918163140),
	 (NULL,'Rodriguez','$2a$10$kYr.Q4Hidg4PxaWOfdJ/VOtee6rLO8RTAp.Fz.el2wXkucg5dB5mq','32472834','2025-11-06 17:28:40.048455',NULL,'1995-11-16 00:00:00.000000',NULL,'sararodriguez@maildrop.cc','Sara','sararodriguez',NULL,NULL,NULL,NULL),
	 (NULL,'Ogás','$2a$10$5oegXgSudPhjucDsBlyhnutupy4P0m2hAor8yb7JUyXd3m8ifChpi','32727392','2025-11-06 17:29:47.405380',NULL,'2001-09-03 00:00:00.000000',NULL,'sebastianogas@maildrop.cc','Sebastián','sebastianogas',NULL,NULL,NULL,NULL),
	 (NULL,'Duran','$2a$10$78VQZ7rNcPjKYN6R9O1IkODrj.EONchZ54RSw0YgzpBgggphdmR/C','37203282','2025-11-07 09:42:33.338614',NULL,'1997-06-20 00:00:00.000000',NULL,'tatianaduran@maildrop.cc','Tatiana','tatianaduran','APP_USR-401897791915576-112214-d7c744e208914af7b57659d561d85523-2918163702','APP_USR-1a10c30c-3b01-454d-b30b-6b9f0f097225','TG-6921ff82f5341f0001229e10-2918163702',2918163702);

INSERT INTO rol_usuario (fecha_hora_alta,fecha_hora_baja,rol_id,usuario_id) VALUES
	 ('2025-11-06 17:18:33.430867',NULL,6,1),
	 ('2025-11-06 17:18:33.430867',NULL,5,1),
	 ('2025-11-06 17:18:33.430867',NULL,4,1),
	 ('2025-11-06 17:18:33.430867',NULL,3,1),
	 ('2025-11-06 17:24:32.932491',NULL,2,2),
	 ('2025-11-06 17:28:40.070522',NULL,2,3),
	 ('2025-11-06 17:30:00.276077',NULL,2,4),
	 ('2025-11-07 09:42:50.542963',NULL,2,5);


-- =========================
-- Programación de backups
-- =========================

INSERT INTO programacion_backup (activa,copias_conservar,copias_incrementales,dias,fecha_desde,fecha_hora_alta,fecha_hora_baja,horas,meses) VALUES
	 (1,3,2,1,'2025-11-29 17:00:00.000000','2025-11-29 16:13:08.717085',NULL,0,0);


