-- Crear tabla de prueba
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);



CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL
);

INSERT INTO clientes (nombre, email) VALUES ('Cliente Prueba 1', 'cliente1@prueba.com');
INSERT INTO clientes (nombre, email) VALUES ('Cliente Prueba 2', 'cliente2@prueba.com');
