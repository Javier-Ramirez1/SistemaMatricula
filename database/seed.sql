-- ============================================================
--  SISTEMA DE MATRÍCULA — Datos base (seed)
--  Ejecutar DESPUÉS de schema.sql
-- ============================================================

USE db_matricula;

INSERT INTO tipo_documento (codigo, nombre) VALUES
  ('DNI', 'Documento Nacional de Identidad'),
  ('CE',  'Carnet de Extranjería'),
  ('PAS', 'Pasaporte');

INSERT INTO rol (nombre, descripcion) VALUES
  ('SUPERUSUARIO', 'Acceso total al sistema'),
  ('DIRECTOR',     'Solo lectura de registros'),
  ('SECRETARIA',   'Operaciones del sistema');

-- usuario admin: cambia este hash por uno real con BCryptPasswordEncoder
-- antes de usar el sistema en serio (ver nota al final del archivo).
INSERT INTO usuario (username, password_hash, nombre_completo, id_tipo_documento, numero_documento, es_superusuario) VALUES
  ('admin', '$2a$10$PLACEHOLDER_HASH', 'Administrador del Sistema', 1, '00000000', 1);

INSERT INTO usuario_rol (id_usuario, id_rol) VALUES (1, 1);

INSERT INTO nivel (nombre) VALUES
  ('INICIAL'),
  ('PRIMARIA'),
  ('SECUNDARIA');

INSERT INTO tipo_concepto (nombre) VALUES
  ('OBLIGATORIO'),
  ('OPCIONAL');

INSERT INTO parametro (codigo, valor, descripcion) VALUES
  ('MAX_ALUMNOS_POR_AULA', '35', 'Máximo de alumnos permitidos por aula'),
  ('HABILITAR_PAGOS_ONLINE', '1', 'Permitir pagos en línea');

INSERT INTO anio_academico (anio, activo) VALUES (2026, 1);

INSERT INTO correlativo (id_anio_academico, prefijo, ultimo_numero) VALUES (1, 'BOL', 0);

-- Permisos atómicos: modulo / submodulo / accion
-- Alimentan el árbol de checkboxes del panel del superusuario.
INSERT INTO permiso (modulo, submodulo, accion, descripcion) VALUES
  ('ACADEMICO', 'ALUMNO', 'CREAR', 'Crear alumno nuevo'),
  ('ACADEMICO', 'ALUMNO', 'LEER', 'Ver alumnos'),
  ('ACADEMICO', 'ALUMNO', 'ACTUALIZAR', 'Modificar datos de alumno'),
  ('ACADEMICO', 'ALUMNO', 'ELIMINAR', 'Eliminar alumno'),
  ('ACADEMICO', 'MATRICULA', 'CREAR', 'Registrar matrícula'),
  ('ACADEMICO', 'MATRICULA', 'LEER', 'Ver matrículas'),
  ('ACADEMICO', 'MATRICULA', 'ACTUALIZAR', 'Modificar matrícula'),
  ('ACADEMICO', 'MATRICULA', 'ANULAR', 'Anular matrícula'),
  ('FINANZAS', 'CONCEPTO', 'CREAR', 'Crear concepto de pago'),
  ('FINANZAS', 'CONCEPTO', 'LEER', 'Ver conceptos'),
  ('FINANZAS', 'CONCEPTO', 'ACTUALIZAR', 'Modificar concepto'),
  ('FINANZAS', 'PAGO', 'PROCESAR', 'Procesar pago'),
  ('FINANZAS', 'PAGO', 'LEER', 'Ver pagos'),
  ('FINANZAS', 'RECIBO', 'EMITIR', 'Generar recibo'),
  ('FINANZAS', 'REPORTE', 'LEER', 'Ver reportes financieros');

-- El SUPERUSUARIO no necesita filas en rol_permiso: en
-- RolService.asignarPermisos() está bloqueado para que nunca se
-- le restrinjan permisos, y en SecurityConfig (revisar) debería
-- tener bypass total por su rol ROLE_SUPERUSUARIO, no por filas
-- individuales en esta tabla.

-- DIRECTOR: solo lectura, como piden los apuntes ("el director
-- solo tiene permiso para ver registro")
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 2, id FROM permiso WHERE accion = 'LEER';

-- SECRETARIA: todas las demás operaciones
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 3, id FROM permiso WHERE accion <> 'LEER';

-- ============================================================
-- NOTA IMPORTANTE sobre el password del admin:
-- '$2a$10$PLACEHOLDER_HASH' NO es un hash BCrypt válido — no vas
-- a poder loguearte con él tal cual. Para generar uno real puedes
-- correr esto en una clase de prueba o un endpoint temporal:
--
--   new BCryptPasswordEncoder().encode("tu_password_aqui")
--
-- y reemplazar el valor de password_hash con el resultado.
-- ============================================================
