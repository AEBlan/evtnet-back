/*CREATE TABLE IF NOT EXISTS Usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    username VARCHAR(100),
    dni VARCHAR(20),
    mail VARCHAR(100),
    fechaNacimiento DATETIME,
    fotoPerfil VARCHAR(255),
    contraseña VARCHAR(255),
    CBU VARCHAR(100),
    fechaHoraAlta DATETIME,
    fechaHoraBaja DATETIME
);

-- Crear tabla de prueba
/* Si queremos hacer testing mas detallado pero hibernate ya hice todo
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE clientes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100),
  email VARCHAR(100) UNIQUE
); */