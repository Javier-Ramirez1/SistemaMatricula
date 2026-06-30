-- ============================================================
--  SISTEMA DE MATRÍCULA — DDL final
--  Motor: MySQL 8.0+ (InnoDB, utf8mb4)
--  Generado para coincidir EXACTAMENTE con las entidades JPA
--  del backend (spring.jpa.hibernate.ddl-auto=validate exige
--  que cada columna/tabla aquí declarada coincida con la entity).
--
--  Notas de traducción respecto al script original (Postgres):
--   - BIGSERIAL          -> BIGINT AUTO_INCREMENT
--   - SMALLINT (1/0)     -> TINYINT  (mapea a Byte en Java)
--   - JSONB               -> JSON     (tipo nativo MySQL 8)
--   - CREATE SCHEMA / search_path -> no aplica en MySQL,
--     se usa la base de datos seleccionada con USE.
--   - Las tablas usuario_rol y rol_permiso son tablas puente
--     SIMPLES (sin PK propio ni auditoría): así las gestiona
--     Hibernate vía @ManyToMany/@JoinTable en Usuario.java y
--     Rol.java. (Las entidades UsuarioRol/RolPermiso que existían
--     en el proyecto eran código duplicado sin uso real — se
--     eliminaron del backend porque esperaban columnas que esta
--     tabla simple no tiene.)
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_matricula
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE db_matricula;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- BLOQUE 0 — SEGURIDAD
-- ============================================================

DROP TABLE IF EXISTS login_log;
DROP TABLE IF EXISTS rol_permiso;
DROP TABLE IF EXISTS usuario_rol;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS permiso;
DROP TABLE IF EXISTS rol;
DROP TABLE IF EXISTS tipo_documento;

CREATE TABLE rol (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre          VARCHAR(50) NOT NULL UNIQUE,
  descripcion     VARCHAR(200),
  estado          TINYINT NOT NULL DEFAULT 1,
  version         BIGINT NOT NULL DEFAULT 0,
  usuario_insert  VARCHAR(50),
  fecha_insert    DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update  VARCHAR(50),
  fecha_update    DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE permiso (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  modulo          VARCHAR(60) NOT NULL,
  submodulo       VARCHAR(60) NOT NULL,
  accion          VARCHAR(60) NOT NULL,
  descripcion     VARCHAR(200),
  estado          TINYINT NOT NULL DEFAULT 1,
  version         BIGINT NOT NULL DEFAULT 0,
  usuario_insert  VARCHAR(50),
  fecha_insert    DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update  VARCHAR(50),
  fecha_update    DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_permiso UNIQUE (modulo, submodulo, accion)
) ENGINE=InnoDB;

CREATE TABLE tipo_documento (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  codigo          VARCHAR(10) NOT NULL UNIQUE,
  nombre          VARCHAR(60) NOT NULL,
  estado          TINYINT NOT NULL DEFAULT 1,
  usuario_insert  VARCHAR(50),
  fecha_insert    DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update  VARCHAR(50),
  fecha_update    DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE usuario (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  username           VARCHAR(50) NOT NULL UNIQUE,
  password_hash      VARCHAR(255) NOT NULL,
  nombre_completo    VARCHAR(150) NOT NULL,
  email              VARCHAR(100),
  id_tipo_documento  BIGINT NOT NULL,
  numero_documento   VARCHAR(20) NOT NULL,
  es_superusuario    TINYINT NOT NULL DEFAULT 0,
  estado             TINYINT NOT NULL DEFAULT 1,
  version            BIGINT NOT NULL DEFAULT 0,
  usuario_insert     VARCHAR(50),
  fecha_insert       DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update     VARCHAR(50),
  fecha_update       DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_usuario_doc UNIQUE (id_tipo_documento, numero_documento),
  CONSTRAINT fk_usuario_tipo_doc FOREIGN KEY (id_tipo_documento) REFERENCES tipo_documento(id)
) ENGINE=InnoDB;

-- Tabla puente simple (Hibernate @ManyToMany en Usuario.roles)
CREATE TABLE usuario_rol (
  id_usuario  BIGINT NOT NULL,
  id_rol      BIGINT NOT NULL,
  PRIMARY KEY (id_usuario, id_rol),
  CONSTRAINT fk_usuariorol_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
  CONSTRAINT fk_usuariorol_rol FOREIGN KEY (id_rol) REFERENCES rol(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tabla puente simple (Hibernate @ManyToMany en Rol.permisos)
CREATE TABLE rol_permiso (
  id_rol      BIGINT NOT NULL,
  id_permiso  BIGINT NOT NULL,
  PRIMARY KEY (id_rol, id_permiso),
  CONSTRAINT fk_rolpermiso_rol FOREIGN KEY (id_rol) REFERENCES rol(id) ON DELETE CASCADE,
  CONSTRAINT fk_rolpermiso_permiso FOREIGN KEY (id_permiso) REFERENCES permiso(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE login_log (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_usuario      BIGINT NOT NULL,
  fecha_login     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_logout    DATETIME,
  ip_cliente      VARCHAR(45),
  navegador       VARCHAR(200),
  resultado       VARCHAR(20) NOT NULL,
  motivo_fallo    VARCHAR(200),
  sesion_id       VARCHAR(100),
  usuario_insert  VARCHAR(50),
  fecha_insert    DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_login_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id)
) ENGINE=InnoDB;


-- ============================================================
-- BLOQUE 1 — ACADÉMICO
-- ============================================================

DROP TABLE IF EXISTS alumno;
DROP TABLE IF EXISTS aula;
DROP TABLE IF EXISTS parametro;
DROP TABLE IF EXISTS seccion;
DROP TABLE IF EXISTS grado;
DROP TABLE IF EXISTS nivel;
DROP TABLE IF EXISTS anio_academico;

CREATE TABLE anio_academico (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  anio            INT NOT NULL UNIQUE,
  activo          TINYINT NOT NULL DEFAULT 1,
  estado          TINYINT NOT NULL DEFAULT 1,
  version         BIGINT NOT NULL DEFAULT 0,
  usuario_insert  VARCHAR(50),
  fecha_insert    DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update  VARCHAR(50),
  fecha_update    DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE nivel (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre          VARCHAR(50) NOT NULL UNIQUE,
  estado          TINYINT NOT NULL DEFAULT 1,
  version         BIGINT NOT NULL DEFAULT 0,
  usuario_insert  VARCHAR(50),
  fecha_insert    DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update  VARCHAR(50),
  fecha_update    DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE grado (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre          VARCHAR(50) NOT NULL,
  id_nivel        BIGINT NOT NULL,
  orden           INT NOT NULL DEFAULT 1,
  estado          TINYINT NOT NULL DEFAULT 1,
  version         BIGINT NOT NULL DEFAULT 0,
  usuario_insert  VARCHAR(50),
  fecha_insert    DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update  VARCHAR(50),
  fecha_update    DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_grado_nivel FOREIGN KEY (id_nivel) REFERENCES nivel(id)
) ENGINE=InnoDB;

CREATE TABLE seccion (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre          VARCHAR(5) NOT NULL UNIQUE,
  estado          TINYINT NOT NULL DEFAULT 1,
  version         BIGINT NOT NULL DEFAULT 0,
  usuario_insert  VARCHAR(50),
  fecha_insert    DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update  VARCHAR(50),
  fecha_update    DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE parametro (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  codigo          VARCHAR(60) NOT NULL UNIQUE,
  valor           VARCHAR(200) NOT NULL,
  descripcion     VARCHAR(200),
  estado          TINYINT NOT NULL DEFAULT 1,
  version         BIGINT NOT NULL DEFAULT 0,
  usuario_insert  VARCHAR(50),
  fecha_insert    DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update  VARCHAR(50),
  fecha_update    DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE aula (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_anio_academico  BIGINT NOT NULL,
  id_nivel           BIGINT NOT NULL,
  id_grado           BIGINT NOT NULL,
  id_seccion         BIGINT NOT NULL,
  cantidad_alumnos   INT NOT NULL DEFAULT 0,
  estado             TINYINT NOT NULL DEFAULT 1,
  version            BIGINT NOT NULL DEFAULT 0,
  usuario_insert     VARCHAR(50),
  fecha_insert       DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update     VARCHAR(50),
  fecha_update       DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_aula UNIQUE (id_anio_academico, id_nivel, id_grado, id_seccion),
  CONSTRAINT fk_aula_anio FOREIGN KEY (id_anio_academico) REFERENCES anio_academico(id),
  CONSTRAINT fk_aula_nivel FOREIGN KEY (id_nivel) REFERENCES nivel(id),
  CONSTRAINT fk_aula_grado FOREIGN KEY (id_grado) REFERENCES grado(id),
  CONSTRAINT fk_aula_seccion FOREIGN KEY (id_seccion) REFERENCES seccion(id)
) ENGINE=InnoDB;

CREATE TABLE alumno (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_tipo_documento  BIGINT NOT NULL,
  numero_documento   VARCHAR(20) NOT NULL,
  apellido_paterno   VARCHAR(80) NOT NULL,
  apellido_materno   VARCHAR(80) NOT NULL,
  nombres            VARCHAR(100) NOT NULL,
  fecha_nacimiento   DATE NOT NULL,
  estado             TINYINT NOT NULL DEFAULT 1,
  version            BIGINT NOT NULL DEFAULT 0,
  usuario_insert     VARCHAR(50),
  fecha_insert       DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update     VARCHAR(50),
  fecha_update       DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_alumno_doc UNIQUE (id_tipo_documento, numero_documento),
  CONSTRAINT fk_alumno_tipo_doc FOREIGN KEY (id_tipo_documento) REFERENCES tipo_documento(id)
) ENGINE=InnoDB;


-- ============================================================
-- BLOQUE 2 — FINANZAS
-- ============================================================

DROP TABLE IF EXISTS recibo;
DROP TABLE IF EXISTS pago;
DROP TABLE IF EXISTS cuota;
DROP TABLE IF EXISTS matricula;
DROP TABLE IF EXISTS correlativo;
DROP TABLE IF EXISTS concepto;
DROP TABLE IF EXISTS tipo_concepto;

CREATE TABLE tipo_concepto (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre          VARCHAR(60) NOT NULL,
  estado          TINYINT NOT NULL DEFAULT 1,
  version         BIGINT NOT NULL DEFAULT 0,
  usuario_insert  VARCHAR(50),
  fecha_insert    DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update  VARCHAR(50),
  fecha_update    DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE concepto (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_anio_academico  BIGINT NOT NULL,
  id_tipo_concepto   BIGINT NOT NULL,
  nombre             VARCHAR(100) NOT NULL,
  monto              DECIMAL(10,2) NOT NULL,
  orden_cobro        INT NOT NULL DEFAULT 1,
  es_default         TINYINT NOT NULL DEFAULT 0,
  estado             TINYINT NOT NULL DEFAULT 1,
  version            BIGINT NOT NULL DEFAULT 0,
  usuario_insert     VARCHAR(50),
  fecha_insert       DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update     VARCHAR(50),
  fecha_update       DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_concepto_anio FOREIGN KEY (id_anio_academico) REFERENCES anio_academico(id),
  CONSTRAINT fk_concepto_tipo FOREIGN KEY (id_tipo_concepto) REFERENCES tipo_concepto(id)
) ENGINE=InnoDB;

CREATE TABLE matricula (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_anio_academico  BIGINT NOT NULL,
  id_alumno          BIGINT NOT NULL,
  id_aula            BIGINT NOT NULL,
  fecha_matricula    DATE NOT NULL,
  estado_matricula   VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',
  estado             TINYINT NOT NULL DEFAULT 1,
  version            BIGINT NOT NULL DEFAULT 0,
  usuario_insert     VARCHAR(50),
  fecha_insert       DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update     VARCHAR(50),
  fecha_update       DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_matricula_alumno_anio UNIQUE (id_alumno, id_anio_academico),
  CONSTRAINT fk_matricula_anio FOREIGN KEY (id_anio_academico) REFERENCES anio_academico(id),
  CONSTRAINT fk_matricula_alumno FOREIGN KEY (id_alumno) REFERENCES alumno(id),
  CONSTRAINT fk_matricula_aula FOREIGN KEY (id_aula) REFERENCES aula(id)
) ENGINE=InnoDB;

CREATE TABLE cuota (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_matricula     BIGINT NOT NULL,
  id_concepto      BIGINT NOT NULL,
  monto            DECIMAL(10,2) NOT NULL,
  orden_cobro      INT NOT NULL,
  estado_pago      VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
  estado           TINYINT NOT NULL DEFAULT 1,
  version          BIGINT NOT NULL DEFAULT 0,
  usuario_insert   VARCHAR(50),
  fecha_insert     DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update   VARCHAR(50),
  fecha_update     DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_cuota_matricula FOREIGN KEY (id_matricula) REFERENCES matricula(id),
  CONSTRAINT fk_cuota_concepto FOREIGN KEY (id_concepto) REFERENCES concepto(id)
) ENGINE=InnoDB;

CREATE TABLE pago (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_cuota         BIGINT NOT NULL,
  id_matricula     BIGINT NOT NULL,
  monto_pagado     DECIMAL(10,2) NOT NULL,
  tipo_pago        VARCHAR(20) NOT NULL,
  referencia_pago  VARCHAR(100),
  fecha_pago       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  estado           VARCHAR(20) NOT NULL DEFAULT 'PROCESADO',
  estado_registro  TINYINT NOT NULL DEFAULT 1,
  version          BIGINT NOT NULL DEFAULT 0,
  usuario_insert   VARCHAR(50),
  fecha_insert     DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update   VARCHAR(50),
  fecha_update     DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_pago_cuota FOREIGN KEY (id_cuota) REFERENCES cuota(id),
  CONSTRAINT fk_pago_matricula FOREIGN KEY (id_matricula) REFERENCES matricula(id)
) ENGINE=InnoDB;

CREATE TABLE recibo (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  numero_boleta    VARCHAR(20) NOT NULL UNIQUE,
  id_pago          BIGINT NOT NULL,
  id_matricula     BIGINT NOT NULL,
  monto_pagado     DECIMAL(10,2) NOT NULL,
  fecha_emision    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  estado           TINYINT NOT NULL DEFAULT 1,
  version          BIGINT NOT NULL DEFAULT 0,
  usuario_insert   VARCHAR(50),
  fecha_insert     DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update   VARCHAR(50),
  fecha_update     DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_recibo_pago FOREIGN KEY (id_pago) REFERENCES pago(id),
  CONSTRAINT fk_recibo_matricula FOREIGN KEY (id_matricula) REFERENCES matricula(id)
) ENGINE=InnoDB;

CREATE TABLE correlativo (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_anio_academico  BIGINT NOT NULL UNIQUE,
  prefijo            VARCHAR(10) NOT NULL DEFAULT 'BOL',
  ultimo_numero      BIGINT NOT NULL DEFAULT 0,
  estado             TINYINT NOT NULL DEFAULT 1,
  version            BIGINT NOT NULL DEFAULT 0,
  usuario_insert     VARCHAR(50),
  fecha_insert       DATETIME DEFAULT CURRENT_TIMESTAMP,
  usuario_update     VARCHAR(50),
  fecha_update       DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_correlativo_anio FOREIGN KEY (id_anio_academico) REFERENCES anio_academico(id)
) ENGINE=InnoDB;


-- ============================================================
-- BLOQUE 3 — AUDITORÍA
-- ============================================================

DROP TABLE IF EXISTS auditoria;

CREATE TABLE auditoria (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  tabla           VARCHAR(60) NOT NULL,
  id_registro     BIGINT NOT NULL,
  accion          VARCHAR(20) NOT NULL,
  datos_antes     JSON,
  datos_despues   JSON,
  usuario         VARCHAR(50) NOT NULL,
  ip_cliente      VARCHAR(45),
  navegador       VARCHAR(200),
  version_entity  BIGINT,
  observacion     VARCHAR(500),
  fecha           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


-- ============================================================
-- ÍNDICES
-- ============================================================

CREATE INDEX idx_grado_nivel ON grado(id_nivel);
CREATE INDEX idx_aula_anio ON aula(id_anio_academico);
CREATE INDEX idx_aula_cantidad ON aula(cantidad_alumnos);
CREATE INDEX idx_alumno_documento ON alumno(id_tipo_documento, numero_documento);
CREATE INDEX idx_alumno_nombre ON alumno(apellido_paterno, apellido_materno, nombres);

CREATE INDEX idx_matricula_alumno ON matricula(id_alumno);
CREATE INDEX idx_matricula_anio ON matricula(id_anio_academico);
CREATE INDEX idx_matricula_aula ON matricula(id_aula);
CREATE INDEX idx_matricula_estado ON matricula(estado_matricula);
CREATE INDEX idx_cuota_matricula ON cuota(id_matricula);
CREATE INDEX idx_cuota_estado_pago ON cuota(estado_pago);
CREATE INDEX idx_cuota_orden ON cuota(id_matricula, orden_cobro);
CREATE INDEX idx_pago_cuota ON pago(id_cuota);
CREATE INDEX idx_pago_fecha ON pago(fecha_pago);
CREATE INDEX idx_pago_estado ON pago(estado);
CREATE INDEX idx_recibo_boleta ON recibo(numero_boleta);
CREATE INDEX idx_recibo_pago ON recibo(id_pago);
CREATE INDEX idx_recibo_fecha ON recibo(fecha_emision);
CREATE INDEX idx_correlativo_anio ON correlativo(id_anio_academico);

CREATE INDEX idx_usuario_tipo_doc ON usuario(id_tipo_documento);
CREATE INDEX idx_login_usuario ON login_log(id_usuario);
CREATE INDEX idx_login_fecha ON login_log(fecha_login);
CREATE INDEX idx_login_ip ON login_log(ip_cliente);

CREATE INDEX idx_auditoria_tabla_id ON auditoria(tabla, id_registro);
CREATE INDEX idx_auditoria_usuario ON auditoria(usuario);
CREATE INDEX idx_auditoria_fecha ON auditoria(fecha);
CREATE INDEX idx_auditoria_accion ON auditoria(accion);

SET FOREIGN_KEY_CHECKS = 1;
