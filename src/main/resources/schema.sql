-- Schema para o Sistema de Gestão de Pedidos (DSS EE 2025/2026)
-- Correr este ficheiro uma vez para criar e popular a base de dados.
-- mysql -u root -p < schema.sql

DROP DATABASE IF EXISTS dss_ee;

CREATE DATABASE dss_ee
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE dss_ee;

-- ----------------------------------------------------------------
-- Tabelas
-- ----------------------------------------------------------------

CREATE TABLE IF NOT EXISTS ingrediente (
    nome                VARCHAR(100) PRIMARY KEY,
    quantidade_em_stock DOUBLE       NOT NULL,
    nivel_minimo        DOUBLE       NOT NULL,
    unidade             VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS produto (
    codigo                     VARCHAR(10)              PRIMARY KEY,
    nome                       VARCHAR(100)             NOT NULL,
    preco                      DOUBLE                   NOT NULL,
    tipo                       ENUM('PRONTO','PREPARADO') NOT NULL,
    tempo_estimado_preparacao  INT                      DEFAULT NULL
);

-- Personalizações disponibilizadas por cada produto
CREATE TABLE IF NOT EXISTS personalizacao (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    produto_codigo  VARCHAR(10)  NOT NULL,
    descricao       VARCHAR(100) NOT NULL,
    categoria       VARCHAR(50)  DEFAULT NULL,
    FOREIGN KEY (produto_codigo) REFERENCES produto(codigo) ON DELETE CASCADE
);

-- Ficha técnica: ingredientes consumidos por produto (classe de associação)
CREATE TABLE IF NOT EXISTS ficha_ingrediente (
    produto_codigo   VARCHAR(10)  NOT NULL,
    ingrediente_nome VARCHAR(100) NOT NULL,
    quantidade       DOUBLE       NOT NULL,
    PRIMARY KEY (produto_codigo, ingrediente_nome),
    FOREIGN KEY (produto_codigo)   REFERENCES produto(codigo) ON DELETE CASCADE,
    FOREIGN KEY (ingrediente_nome) REFERENCES ingrediente(nome)
);

CREATE TABLE IF NOT EXISTS pedido (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    data_hora DATETIME                                              NOT NULL,
    estado    ENUM('EM_REGISTO','EM_PREPARACAO','PRONTO','ENTREGUE') NOT NULL
);

CREATE TABLE IF NOT EXISTS item_pedido (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id          INT          NOT NULL,
    produto_codigo     VARCHAR(10)  NOT NULL,
    quantidade         INT          NOT NULL,
    estado_preparacao  ENUM('PENDENTE','PRONTO') NOT NULL,
    preco_unitario     DOUBLE       NOT NULL,
    FOREIGN KEY (pedido_id)      REFERENCES pedido(id),
    FOREIGN KEY (produto_codigo) REFERENCES produto(codigo)
);

-- Personalizações seleccionadas por item de pedido
CREATE TABLE IF NOT EXISTS item_personalizacao (
    item_id          INT NOT NULL,
    personalizacao_id INT NOT NULL,
    PRIMARY KEY (item_id, personalizacao_id),
    FOREIGN KEY (item_id)           REFERENCES item_pedido(id),
    FOREIGN KEY (personalizacao_id) REFERENCES personalizacao(id)
);

CREATE TABLE IF NOT EXISTS funcionario (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nome   VARCHAR(100) NOT NULL UNIQUE,
    funcao ENUM('BALCAO','PREPARACAO','GESTOR') NOT NULL
);

-- ----------------------------------------------------------------
-- Dados iniciais (catálogo, stock e utilizadores)
-- ----------------------------------------------------------------

INSERT IGNORE INTO funcionario (nome, funcao) VALUES
    ('Admin',  'GESTOR'),
    ('Ana',    'BALCAO'),
    ('Bruno',  'BALCAO'),
    ('Carla',  'PREPARACAO'),
    ('David',  'PREPARACAO');

INSERT IGNORE INTO ingrediente (nome, quantidade_em_stock, nivel_minimo, unidade) VALUES
    ('Café',          5000, 500,  'g'),
    ('Pão de trigo',   100,  20,  'unid'),
    ('Manteiga',       2000, 200, 'g'),
    ('Fiambre',        1500, 300, 'g'),
    ('Queijo',         1000, 200, 'g'),
    ('Farinha',        5000, 500, 'g'),
    ('Água 0.5L',       200,  20, 'unid');

-- ----------------------------------------------------------------
-- Trigger para impedir a remoção do último ingrediente de um produto
-- ----------------------------------------------------------------

DROP TRIGGER IF EXISTS trg_impedir_remocao_ultimo_ingrediente;

DELIMITER //
CREATE TRIGGER trg_impedir_remocao_ultimo_ingrediente
BEFORE DELETE ON ficha_ingrediente
FOR EACH ROW
BEGIN
    IF COALESCE(@a_eliminar_produto, 0) = 0 AND
       (SELECT COUNT(*) FROM ficha_ingrediente
        WHERE produto_codigo = OLD.produto_codigo) <= 1 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Um produto tem de ter pelo menos um ingrediente na ficha técnica';
    END IF;
END //
DELIMITER ;

-- ----------------------------------------------------------------
-- Stored procedure para inserir produto com pelo menos um ingrediente
-- ----------------------------------------------------------------

DROP PROCEDURE IF EXISTS inserir_produto;
DROP PROCEDURE IF EXISTS eliminar_produto;

DELIMITER //
CREATE PROCEDURE inserir_produto(
    IN p_codigo  VARCHAR(10),
    IN p_nome    VARCHAR(100),
    IN p_preco   DOUBLE,
    IN p_tipo    ENUM('PRONTO','PREPARADO'),
    IN p_tempo   INT,
    IN p_ing_nome VARCHAR(100),
    IN p_ing_qtd  DOUBLE
)
BEGIN
    IF p_ing_nome IS NULL OR TRIM(p_ing_nome) = '' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Um produto tem de ter pelo menos um ingrediente na ficha técnica';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM ingrediente WHERE nome = p_ing_nome) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Ingrediente não existe na tabela ingrediente';
    END IF;

    INSERT INTO produto (codigo, nome, preco, tipo, tempo_estimado_preparacao)
        VALUES (p_codigo, p_nome, p_preco, p_tipo, p_tempo);

    INSERT INTO ficha_ingrediente (produto_codigo, ingrediente_nome, quantidade)
        VALUES (p_codigo, p_ing_nome, p_ing_qtd);
END //

CREATE PROCEDURE eliminar_produto(IN p_codigo VARCHAR(10))
BEGIN
    IF NOT EXISTS (SELECT 1 FROM produto WHERE codigo = p_codigo) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Produto não encontrado';
    END IF;

    IF EXISTS (SELECT 1 FROM item_pedido WHERE produto_codigo = p_codigo) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Não é possível eliminar um produto com pedidos associados';
    END IF;

    SET @a_eliminar_produto = 1;
    DELETE FROM produto WHERE codigo = p_codigo;
    SET @a_eliminar_produto = 0;
END //

DELIMITER ;

-- ----------------------------------------------------------------
-- Catálogo inicial — produtos inseridos via stored procedure
-- (garante que cada produto tem pelo menos um ingrediente)
-- ----------------------------------------------------------------

CALL inserir_produto('P001', 'Pão',              0.30, 'PRONTO',    NULL, 'Pão de trigo', 1);

CALL inserir_produto('P002', 'Bolo de manteiga', 1.50, 'PRONTO',    NULL, 'Farinha',      80);
INSERT INTO ficha_ingrediente (produto_codigo, ingrediente_nome, quantidade) VALUES ('P002', 'Manteiga', 30);

CALL inserir_produto('P003', 'Água 0.5L',        0.80, 'PRONTO',    NULL, 'Água 0.5L',   1);

CALL inserir_produto('P004', 'Torrada',           1.20, 'PREPARADO',    5, 'Pão de trigo', 1);
INSERT INTO ficha_ingrediente (produto_codigo, ingrediente_nome, quantidade) VALUES ('P004', 'Manteiga', 10);

CALL inserir_produto('P005', 'Café',              0.80, 'PREPARADO',    3, 'Café',         7);

CALL inserir_produto('P006', 'Sandes',            2.50, 'PREPARADO',   10, 'Pão de trigo', 1);
INSERT INTO ficha_ingrediente (produto_codigo, ingrediente_nome, quantidade) VALUES
    ('P006', 'Fiambre', 50),
    ('P006', 'Queijo',  30);

INSERT IGNORE INTO personalizacao (produto_codigo, descricao, categoria) VALUES
    ('P004', 'Com manteiga',  'manteiga'),
    ('P004', 'Sem manteiga',  'manteiga'),
    ('P005', 'Normal',        'intensidade'),
    ('P005', 'Cheio',         'intensidade'),
    ('P005', 'Curto',         'intensidade'),
    ('P006', 'Pão branco',    'pão'),
    ('P006', 'Pão integral',  'pão'),
    ('P006', 'Com fiambre',   'recheio'),
    ('P006', 'Com queijo',    'recheio');
