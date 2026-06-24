CREATE DATABASE IF NOT EXISTS petshop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE petshop_db;

CREATE TABLE proprietario (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              nome VARCHAR(100) NOT NULL,
                              endereco VARCHAR(200),
                              telefone VARCHAR(20),
                              email VARCHAR(100),
                              data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE animal (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        nome VARCHAR(100) NOT NULL,
                        especie VARCHAR(50) NOT NULL,
                        raca VARCHAR(50),
                        idade INT,
                        sexo ENUM('M', 'F') NOT NULL,
                        peso DECIMAL(5,2),
                        foto_path VARCHAR(255),
                        proprietario_id INT NOT NULL,
                        data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (proprietario_id) REFERENCES proprietario(id) ON DELETE CASCADE
);

CREATE TABLE servico (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         nome VARCHAR(100) NOT NULL,
                         descricao TEXT,
                         preco DECIMAL(10,2) NOT NULL
);

CREATE TABLE lancamento_servico (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    animal_id INT NOT NULL,
                                    servico_id INT NOT NULL,
                                    data_lancamento DATE NOT NULL,
                                    valor DECIMAL(10,2) NOT NULL,
                                    observacoes TEXT,
                                    FOREIGN KEY (animal_id) REFERENCES animal(id) ON DELETE CASCADE,
                                    FOREIGN KEY (servico_id) REFERENCES servico(id) ON DELETE CASCADE
);

-- Dados de exemplo
INSERT INTO proprietario (nome, endereco, telefone, email) VALUES
                                                               ('João Silva', 'Rua das Flores, 123', '(32) 99999-0001', 'joao@email.com'),
                                                               ('Maria Souza', 'Av. Central, 456', '(32) 99999-0002', 'maria@email.com');

INSERT INTO animal (nome, especie, raca, idade, sexo, peso, proprietario_id) VALUES
                                                                                 ('Rex', 'Cachorro', 'Labrador', 3, 'M', 28.5, 1),
                                                                                 ('Mimi', 'Gato', 'Persa', 2, 'F', 4.2, 2);

INSERT INTO servico (nome, descricao, preco) VALUES
                                                 ('Banho', 'Banho completo com shampoo e condicionador', 50.00),
                                                 ('Tosa', 'Tosa higiênica ou completa', 70.00),
                                                 ('Consulta Veterinária', 'Consulta clínica geral', 120.00),
                                                 ('Vacinação', 'Aplicação de vacinas', 80.00);