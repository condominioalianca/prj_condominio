-- Script de Migração para Nova Estrutura de Comprovantes (PostgreSQL)

-- 1. Criação da nova tabela tb_comprovante
CREATE TABLE IF NOT EXISTS tb_comprovante (
    id_comprovante BIGSERIAL PRIMARY KEY,
    nome_arquivo VARCHAR(255) NOT NULL,
    tipo_arquivo VARCHAR(100),
    dados BYTEA NOT NULL
);

-- Criar índice para buscas por nome do arquivo
CREATE INDEX IF NOT EXISTS idx_comprovante_nome ON tb_comprovante (nome_arquivo);

-- 2. Alteração na tabela tb_extrato
-- 2.1 Adicionar a nova coluna id_comprovante
ALTER TABLE tb_extrato ADD COLUMN IF NOT EXISTS id_comprovante BIGINT;

-- 2.2 Adicionar a restrição de Foreign Key
ALTER TABLE tb_extrato 
    ADD CONSTRAINT fk_extrato_comprovante 
    FOREIGN KEY (id_comprovante) 
    REFERENCES tb_comprovante(id_comprovante);

-- 2.3 Remover a coluna antiga de bytes 
-- (ATENÇÃO: Este comando apagará permanentemente os comprovantes que já estavam no banco)
ALTER TABLE tb_extrato DROP COLUMN IF EXISTS comprovante;
