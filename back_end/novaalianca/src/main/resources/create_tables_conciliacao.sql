-- Script DDL para criar a estrutura do banco de dados para a Conciliação Bancária

-- 1. Cria a tabela de conciliação
CREATE TABLE IF NOT EXISTS tb_conciliacao (
    id_conciliacao BIGSERIAL PRIMARY KEY,
    dt_referencia DATE,
    descricao VARCHAR(255),
    status INT,
    status_geral INT,
    dt_criacao TIMESTAMP,
    usuario_criacao VARCHAR(255),
    dt_atualizacao TIMESTAMP,
    usuario_atualizacao VARCHAR(255)
);

-- 2. Cria os índices na tabela de conciliação
CREATE INDEX IF NOT EXISTS idx_conciliacao_status ON tb_conciliacao (status);
CREATE INDEX IF NOT EXISTS idx_conciliacao_data ON tb_conciliacao (dt_referencia);

-- 3. Adiciona as colunas novas na tabela de extrato
ALTER TABLE tb_extrato ADD COLUMN IF NOT EXISTS conciliacao_id BIGINT;
ALTER TABLE tb_extrato ADD COLUMN IF NOT EXISTS comprovante BYTEA;
ALTER TABLE tb_extrato ADD COLUMN IF NOT EXISTS status_conciliado INT;
ALTER TABLE tb_extrato ADD COLUMN IF NOT EXISTS status_geral INT;

-- 4. Adiciona a chave estrangeira na tabela de extrato
ALTER TABLE tb_extrato 
    ADD CONSTRAINT fk_extrato_conciliacao 
    FOREIGN KEY (conciliacao_id) 
    REFERENCES tb_conciliacao (id_conciliacao);
