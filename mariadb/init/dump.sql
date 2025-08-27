USE `evtnet_db`;

-- ParametroSistema
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

-- MedioDePago
INSERT INTO medio_de_pago (nombre)
  SELECT 'Mercado Pago' WHERE NOT EXISTS (SELECT 1 FROM medio_de_pago WHERE nombre='Mercado Pago');

-- ConfiguracionCPI (asumo 1 sola config)
INSERT INTO comision_por_inscripcion (monto_limite, porcentaje)
  SELECT 100000.00, 5.00
  WHERE NOT EXISTS (SELECT 1 FROM comision_por_inscripcion);

-- ConfiguracionCPO (asumo 1 sola config)
INSERT INTO comision_por_organizacion (monto_limite, porcentaje)
  SELECT 250000.00, 7.50
  WHERE NOT EXISTS (SELECT 1 FROM comision_por_organizacion);

-- Disciplina
INSERT INTO disciplina (nombre) SELECT 'Futbol'  WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Futbol');
INSERT INTO disciplina (nombre) SELECT 'Padel'   WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Padel');
INSERT INTO disciplina (nombre) SELECT 'Metegol' WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nombre='Metegol');

-- ModoEvento
INSERT INTO modo_evento (nombre) SELECT 'Por equipos'  WHERE NOT EXISTS (SELECT 1 FROM modo_evento WHERE nombre='Por equipos');
INSERT INTO modo_evento (nombre) SELECT 'Cooperativo'  WHERE NOT EXISTS (SELECT 1 FROM modo_evento WHERE nombre='Cooperativo');
INSERT INTO modo_evento (nombre) SELECT 'Individual'   WHERE NOT EXISTS (SELECT 1 FROM modo_evento WHERE nombre='Individual');

-- TipoInscripcion
INSERT INTO tipo_inscripcion_evento (nombre) SELECT 'Inscripción por Usuario'         WHERE NOT EXISTS (SELECT 1 FROM tipo_inscripcion_evento WHERE nombre='Inscripción por Usuario');
INSERT INTO tipo_inscripcion_evento (nombre) SELECT 'Inscripcion por Administrador'   WHERE NOT EXISTS (SELECT 1 FROM tipo_inscripcion_evento WHERE nombre='Inscripcion por Administrador');
INSERT INTO tipo_inscripcion_evento (nombre) SELECT 'Inscripcion Usuario/Administrador' WHERE NOT EXISTS (SELECT 1 FROM tipo_inscripcion_evento WHERE nombre='Inscripcion Usuario/Administrador');

-- EstadoDenunciaEvento
INSERT INTO estado_denuncia_evento (nombre) SELECT 'Ingresado'  WHERE NOT EXISTS (SELECT 1 FROM estado_denuncia_evento WHERE nombre='Ingresado');
INSERT INTO estado_denuncia_evento (nombre) SELECT 'Finalizado' WHERE NOT EXISTS (SELECT 1 FROM estado_denuncia_evento WHERE nombre='Finalizado');

-- CalificacionTipo
INSERT INTO calificacion_tipo (nombre) SELECT 'Normal'  WHERE NOT EXISTS (SELECT 1 FROM calificacion_tipo WHERE nombre='Normal');
INSERT INTO calificacion_tipo (nombre) SELECT 'Denuncia' WHERE NOT EXISTS (SELECT 1 FROM calificacion_tipo WHERE nombre='Denuncia');


-- TipoUsuarioGrupo
INSERT INTO tipo_usuario_grupo (nombre) SELECT 'Miembro'       WHERE NOT EXISTS (SELECT 1 FROM tipo_usuario_grupo WHERE nombre='Miembro');
INSERT INTO tipo_usuario_grupo (nombre) SELECT 'Administrador' WHERE NOT EXISTS (SELECT 1 FROM tipo_usuario_grupo WHERE nombre='Administrador');

-- EstadoSEP
INSERT INTO estadosep (nombre) SELECT 'Pendiente' WHERE NOT EXISTS (SELECT 1 FROM estadosep WHERE nombre='Pendiente');
INSERT INTO estadosep (nombre) SELECT 'Aprobada'  WHERE NOT EXISTS (SELECT 1 FROM estadosep WHERE nombre='Aprobada');
INSERT INTO estadosep (nombre) SELECT 'Rechazada' WHERE NOT EXISTS (SELECT 1 FROM estadosep WHERE nombre='Rechazada');

-- TipoEspacio
INSERT INTO tipo_espacio (nombre) SELECT 'Privado' WHERE NOT EXISTS (SELECT 1 FROM tipo_espacio WHERE nombre='Privado');
INSERT INTO tipo_espacio (nombre) SELECT 'Público' WHERE NOT EXISTS (SELECT 1 FROM tipo_espacio WHERE nombre='Público');

-- IconoCaracteristica

-- TipoExcepcionHorarioEspacio
INSERT INTO tipo_excepcion_horario_espacio (nombre) SELECT 'Completa'
  WHERE NOT EXISTS (SELECT 1 FROM tipo_excepcion_horario_espacio WHERE nombre='Completa');
INSERT INTO tipo_excepcion_horario_espacio (nombre) SELECT 'Externa'
  WHERE NOT EXISTS (SELECT 1 FROM tipo_excepcion_horario_espacio WHERE nombre='Externa');

-- -------------------------
-- Permisos base (todos los que listaste)
-- -------------------------
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

-- -------------------------
-- Roles
-- -------------------------
INSERT INTO rol (nombre) SELECT 'PendienteConfirmación' WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre='PendienteConfirmación');
INSERT INTO rol (nombre) SELECT 'Usuario' WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre='Usuario');
INSERT INTO rol (nombre) SELECT 'Administrador' WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre='Administrador');
INSERT INTO rol (nombre) SELECT 'SuperAdministrador' WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre='SuperAdministrador');
INSERT INTO rol (nombre) SELECT 'Perito' WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre='Perito');

-- -------------------------
-- Rol-Permiso mapping
-- -------------------------

-- 1) PendienteConfirmación -> solo HabilitarCuenta
SET @rol = (SELECT id FROM rol WHERE nombre='PendienteConfirmación');
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT @rol, p.id FROM permiso p
WHERE p.nombre='HabilitarCuenta'
AND NOT EXISTS (SELECT 1 FROM rol_permiso rp WHERE rp.rol_id=@rol AND rp.permiso_id=p.id);

-- 2) Usuario -> permisos básicos
SET @rol = (SELECT id FROM rol WHERE nombre='Usuario');
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT @rol, p.id FROM permiso p
WHERE p.nombre IN (
  'InicioSesion','VisionPerfilPropio','ModificacionPerfilPropio','VisionPerfilTercero',
  'InscripcionEventos','VisionEventos','OrganizacionEventos','AdministracionEventos',
  'VisionEspacios','CreacionEspaciosPrivados','AdministracionEspaciosPrivados',
  'ParticipacionGrupos','CreacionGrupos','AdministracionGrupos','SolicitudEspaciosPublicos',
  'CalificacionUsuarios','DenunciaEventos','ResenaEspacios','VisionReportes'
)
AND NOT EXISTS (SELECT 1 FROM rol_permiso rp WHERE rp.rol_id=@rol AND rp.permiso_id=p.id);

-- 3) Administrador -> permisos avanzados
SET @rol = (SELECT id FROM rol WHERE nombre='Administrador');
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT @rol, p.id FROM permiso p
WHERE p.nombre IN (
  'InicioSesion','VisionPerfilPropio','ModificacionPerfilPropio','VisionPerfilTerceroCompleta',
  'VisionEventos','VisionEspacios','ParticipacionGrupos','CreacionGrupos','AdministracionGrupos',
  'AdministracionEspaciosPublicos','SolicitudEspaciosPublicos','AdministracionParametros',
  'AdministracionMascota','AdministracionRoles','AdministracionUsuarios','RealizacionBackup',
  'VisionLogUsuariosGrupos','VisionLogEventos','VisionLogEspacios'
)
AND NOT EXISTS (SELECT 1 FROM rol_permiso rp WHERE rp.rol_id=@rol AND rp.permiso_id=p.id);

-- 4) SuperAdministrador -> permisos globales
SET @rol = (SELECT id FROM rol WHERE nombre='SuperAdministrador');
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT @rol, p.id FROM permiso p
WHERE p.nombre IN (
  'VisionLogPagos','VisionLogParametros','AdministracionRolesReservados','VisionReportesGenerales'
)
AND NOT EXISTS (SELECT 1 FROM rol_permiso rp WHERE rp.rol_id=@rol AND rp.permiso_id=p.id);

-- 5) Perito -> solo visión de logs
SET @rol = (SELECT id FROM rol WHERE nombre='Perito');
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT @rol, p.id FROM permiso p
WHERE p.nombre IN (
  'VisionLogUsuariosGrupos','VisionLogEventos','VisionLogEspacios','VisionLogPagos','VisionLogParametros'
)
AND NOT EXISTS (SELECT 1 FROM rol_permiso rp WHERE rp.rol_id=@rol AND rp.permiso_id=p.id);
