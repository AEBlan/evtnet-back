USE `evtnet_db`;

-- =========================
-- ParametroSistema (sin fecha en tu entidad: lo dejo igual)
-- =========================
INSERT INTO parametro_sistema (identificador, nombre, valor)
  SELECT 'longitudPagina', 'longitudPagina', '20' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='longitudPagina');
INSERT INTO parametro_sistema (identificador, nombre, valor)
  SELECT 'eventsMascota', 'eventsMascota', 'load,click,focus,blur' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='eventsMascota');
INSERT INTO parametro_sistema (identificador, nombre, valor)
  SELECT 'c_u', 'c_u', '0.4' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='c_u');
INSERT INTO parametro_sistema (identificador, nombre, valor)
  SELECT 'c_d', 'c_d', '0.35' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='c_d');
INSERT INTO parametro_sistema (identificador, nombre, valor)
  SELECT 'c_p', 'c_p', '0.25' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='c_p');
INSERT INTO parametro_sistema (identificador, nombre, valor)
  SELECT 'c_e','c_e', '0.3' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='c_e');
INSERT INTO parametro_sistema (identificador, nombre, valor)
  SELECT 'dias_previos_resenas_orden', 'dias_previos_resenas_orden', '365' WHERE NOT EXISTS (SELECT 1 FROM parametro_sistema WHERE identificador='dias_previos_resenas_orden');


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
-- EstadoDenunciaEvento (fecha_hora_alta nullable)
-- =========================
INSERT INTO estado_denuncia_evento (nombre, descripcion, fecha_hora_alta)
SELECT 'Ingresado', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM estado_denuncia_evento WHERE nombre='Ingresado');

INSERT INTO estado_denuncia_evento (nombre, descripcion, fecha_hora_alta)
SELECT 'Finalizado', NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM estado_denuncia_evento WHERE nombre='Finalizado');

-- ========================
-- EstadoEspacio
-- ========================
INSERT INTO estado_espacio (id, nombre, descripcion)
SELECT 1, 'En_revisión', "Se revisan los datos correspondientes al espacio"
WHERE NOT EXISTS (SELECT 1 FROM estado_espacio WHERE nombre='En_revisión')

INSERT INTO estado_espacio (id, nombre, descripcion)
SELECT 2, 'Habilitado', "Se habilita el espacio para que se puedan organizar eventos en él"
WHERE NOT EXISTS (SELECT 1 FROM estado_espacio WHERE nombre='Habilitado')

INSERT INTO estado_espacio (id, nombre, descripcion)
SELECT 3, 'Oculto', "Se oculta el espacio para que no se puedan organizar eventos"
WHERE NOT EXISTS (SELECT 1 FROM estado_espacio WHERE nombre='Oculto')

INSERT INTO estado_espacio (id, nombre, descripcion)
SELECT 4, 'Observado', "Se observa para que el dueño revise los datos en caso de que alguno esté incorrecto o falte"
WHERE NOT EXISTS (SELECT 1 FROM estado_espacio WHERE nombre='Observado')

INSERT INTO estado_espacio (id, nombre, descripcion)
SELECT 5, 'Rechazado', "Se rechaza el espacio para que no se puedan organizar eventos"
WHERE NOT EXISTS (SELECT 1 FROM estado_espacio WHERE nombre='Rechazado')

INSERT INTO estado_espacio (id, nombre, descripcion)
SELECT 6, 'Clausurado', "Se clausura el espacio debido a irregularidades en el mismo"
WHERE NOT EXISTS (SELECT 1 FROM estado_espacio WHERE nombre='Clausurado')



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
-- EstadoSEP  
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
-- TipoAdministradorEspacio 
-- =========================
INSERT INTO tipo_administrador_espacio (nombre)
SELECT "Propietario"
WHERE NOT EXISTS (SELECT 1 FROM tipo_administrador_espacio WHERE nombre='Propietario');

INSERT INTO tipo_administrador_espacio (nombre)
SELECT "Administrador"
WHERE NOT EXISTS (SELECT 1 FROM tipo_administrador_espacio WHERE nombre='Administrador');

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
  'VisionLogUsuariosGrupos','VisionLogEventos','VisionLogEspacios', 'DenunciaEventos'
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


-- ===================================================================
-- Registros, tipos y subtipos
-- ===================================================================

INSERT INTO registro (nombre, nombre_formateado)
  SELECT "UsuariosGrupos", "Usuarios y Grupos";

INSERT INTO entidad_registro (nombre)
  SELECT "usuario";

INSERT INTO entidad_registro (nombre)
  SELECT "grupo";

INSERT INTO entidad_registro (nombre)
  SELECT "inicio_sesion";

INSERT INTO entidad_registro (nombre)
  SELECT "calificacion";

INSERT INTO entidad_registro (nombre)
  SELECT "usuario_grupo";
  



INSERT INTO registro (nombre, nombre_formateado)
  SELECT "Eventos", "Eventos";

INSERT INTO entidad_registro (nombre)
  SELECT "evento";

INSERT INTO entidad_registro (nombre)
  SELECT "superevento";

INSERT INTO entidad_registro (nombre)
  SELECT "inscripcion";

INSERT INTO entidad_registro (nombre)
  SELECT "denuncia";

INSERT INTO entidad_registro (nombre)
  SELECT "administrador_evento";

INSERT INTO entidad_registro (nombre)
  SELECT "administrador_superevento";



INSERT INTO registro (nombre, nombre_formateado)
  SELECT "Espacios", "Espacios";

INSERT INTO entidad_registro (nombre)
  SELECT "espacio_privado";
  
INSERT INTO entidad_registro (nombre)
  SELECT "espacio_publico";
  
INSERT INTO entidad_registro (nombre)
  SELECT "subespacio";
  
INSERT INTO entidad_registro (nombre)
  SELECT "solicitud_espacio_publico";
  
INSERT INTO entidad_registro (nombre)
  SELECT "cronograma";
  
INSERT INTO entidad_registro (nombre)
  SELECT "reseña";
  
INSERT INTO entidad_registro (nombre)
  SELECT "administrador_espacio_privado";
  
INSERT INTO entidad_registro (nombre)
  SELECT "encargado_subespacio";
  



INSERT INTO registro (nombre, nombre_formateado)
  SELECT "Pagos", "Pagos";

INSERT INTO entidad_registro (nombre)
  SELECT "pago";

INSERT INTO entidad_registro (nombre)
  SELECT "devolucion";
  
INSERT INTO entidad_registro (nombre)
  SELECT "cobro_comision";
  
INSERT INTO entidad_registro (nombre)
  SELECT "pago_comision";
  



INSERT INTO registro (nombre, nombre_formateado)
  SELECT "Parametros", "Parámetros";

INSERT INTO entidad_registro (nombre)
  SELECT "tipo_calificacion";
  
INSERT INTO entidad_registro (nombre)
  SELECT "motivo_calificacion";
  
INSERT INTO entidad_registro (nombre)
  SELECT "disciplina";
  
INSERT INTO entidad_registro (nombre)
  SELECT "rol";
  
INSERT INTO entidad_registro (nombre)
  SELECT "icono_caracteristica";
  
INSERT INTO entidad_registro (nombre)
  SELECT "comision_inscripcion";
  
INSERT INTO entidad_registro (nombre)
  SELECT "comision_organizacion";
  
INSERT INTO entidad_registro (nombre)
  SELECT "parametro_sistema";
  
INSERT INTO entidad_registro (nombre)
  SELECT "imagen_mascota";
  
INSERT INTO entidad_registro (nombre)
  SELECT "instancia_mascota";




INSERT INTO accion_registro (nombre)
  SELECT "creacion";

INSERT INTO accion_registro (nombre)
  SELECT "eliminacion";

INSERT INTO accion_registro (nombre)
  SELECT "modificacion";

INSERT INTO accion_registro (nombre)
  SELECT "restauracion";

INSERT INTO accion_registro (nombre)
  SELECT "ejecucion";