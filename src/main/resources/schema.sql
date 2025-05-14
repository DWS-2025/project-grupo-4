-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS casino_db;
USE casino_db;

-- Asegurarse de que las tablas se crean con el motor InnoDB para soporte de transacciones
SET foreign_key_checks = 0;

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    balance DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de juegos
CREATE TABLE IF NOT EXISTS games (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     title VARCHAR(255) NOT NULL,
    description TEXT,
    image LONGBLOB,
    chance INT NOT NULL,
    min_input INT NOT NULL,
    multiplier INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de apuestas
CREATE TABLE IF NOT EXISTS bets (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    user_id BIGINT NOT NULL,
                                    game_id BIGINT,
                                    amount INT NOT NULL,
    revenue INT DEFAULT 0,
    game_title VARCHAR(255),
    status BOOLEAN DEFAULT false,
    ver BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (game_id) REFERENCES games(id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de premios
CREATE TABLE IF NOT EXISTS prizes (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      bet_id BIGINT NOT NULL,
                                      amount DECIMAL(10,2) NOT NULL,
    claimed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bet_id) REFERENCES bets(id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Habilitar nuevamente la verificación de claves foráneas
SET foreign_key_checks = 1;