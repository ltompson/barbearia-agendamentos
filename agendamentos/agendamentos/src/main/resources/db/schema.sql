-- ================================
-- SCHEMA DO BANCO DE DADOS
-- Sistema de Agendamentos Barbearia
-- ================================

-- Tabela de clientes
CREATE TABLE clientes (
    id        BIGSERIAL    PRIMARY KEY,
    nome      VARCHAR(100) NOT NULL,
    telefone  VARCHAR(20)  NOT NULL,
    criado_em TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Tabela de admins
CREATE TABLE admins (
    id        BIGSERIAL    PRIMARY KEY,
    nome      VARCHAR(100) NOT NULL,
    usuario   VARCHAR(50)  NOT NULL UNIQUE,
    senha     VARCHAR(255) NOT NULL,
    criado_em TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Tabela de barbeiros
CREATE TABLE barbeiros (
    id        BIGSERIAL    PRIMARY KEY,
    nome      VARCHAR(100) NOT NULL,
    telefone  VARCHAR(20)  NOT NULL,
    ativo     BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Tabela de serviços
CREATE TABLE servicos (
    id              BIGSERIAL    PRIMARY KEY,
    nome            VARCHAR(100) NOT NULL,
    preco           DECIMAL(8,2) NOT NULL,
    duracao_minutos INTEGER      NOT NULL,
    ativo           BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em       TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Tabela de agendamentos
CREATE TABLE agendamentos (
    id          BIGSERIAL   PRIMARY KEY,
    cliente_id  BIGINT      NOT NULL REFERENCES clientes(id),
    barbeiro_id BIGINT      NOT NULL REFERENCES barbeiros(id),
    servico_id  BIGINT      NOT NULL REFERENCES servicos(id),
    data_hora   TIMESTAMP   NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    criado_em   TIMESTAMP   NOT NULL DEFAULT NOW()
);