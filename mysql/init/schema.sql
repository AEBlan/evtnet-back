-- Crear tabla de prueba
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

-- Insertar un usuario de prueba
INSERT INTO usuarios (nombre, email) VALUES
('Usuario de Prueba', 'prueba@evtnet.com');
