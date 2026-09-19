-- ==============================================================================
-- Script de Otimização de Índices para o Dashboard (PostgreSQL)
-- Objetivo: Acelerar filtros temporais por mês e agregações de extratos bancários.
-- ==============================================================================

-- 1. Índice para consultas filtrando pela data de transação
CREATE INDEX IF NOT EXISTS idx_tb_extrato_dt_transacao 
  ON tb_extrato (dt_transacao);

-- 2. Índice para consultas filtrando pela data de inclusão
CREATE INDEX IF NOT EXISTS idx_tb_extrato_dt_inclusao 
  ON tb_extrato (dt_inclusao);

-- 3. Índice composto cobrindo data de transação + tipo de operação + valor
-- Permite que o PostgreSQL realize Index Only Scan ao somar receitas ('C') e despesas ('D') em um mês
CREATE INDEX IF NOT EXISTS idx_tb_extrato_periodo_operacao 
  ON tb_extrato (dt_transacao, tp_operacao, vl_transacao);

-- 4. Índice para busca e agrupamento dos maiores ofensores (débitos por recebedor)
CREATE INDEX IF NOT EXISTS idx_tb_extrato_debito_ofensores 
  ON tb_extrato (dt_transacao, tp_operacao, nome_recebedor);

-- 5. Adiciona coluna 'tipo' (char de 1 caracter: 'C' para Crédito ou 'D' para Débito) na tabela tb_categoria_gasto
ALTER TABLE tb_categoria_gasto ADD COLUMN IF NOT EXISTS tipo VARCHAR(1) DEFAULT 'D';

-- Define Débito ('D') como padrão para todas que estão sem tipo
UPDATE tb_categoria_gasto SET tipo = 'D' WHERE tipo IS NULL;

-- Atualiza para Crédito ('C') as categorias de receita existentes na base
UPDATE tb_categoria_gasto 
SET tipo = 'C' 
WHERE descricao ILIKE '%condominial%' 
   OR descricao ILIKE '%multa%' 
   OR descricao ILIKE '%rendimento%' 
   OR descricao ILIKE '%receita%' 
   OR descricao ILIKE '%fundo de reserva%' 
   OR descricao ILIKE '%mudan%' 
   OR descricao ILIKE '%espa%';

-- Carga para categorias de Crédito ('C') caso ainda não existam
INSERT INTO tb_categoria_gasto (descricao, tipo, ativo)
SELECT d.descricao, 'C', true
FROM (VALUES 
    ('Taxa Condominial'),
    ('Fundo de Reserva'),
    ('Multas e Juros'),
    ('Rendimento de Aplicação'),
    ('Taxa de Mudança / Espaço'),
    ('Outras Receitas')
) AS d(descricao)
WHERE NOT EXISTS (
    SELECT 1 FROM tb_categoria_gasto c WHERE c.descricao ILIKE d.descricao
);

-- 6. Atualização para extratos de boletos condominiais existentes na base:
-- Vincula os boletos à categoria 'Taxa Condominial' e define o status conciliado como BATIDO (1 no JPA EnumType.ORDINAL)
DO $$
DECLARE
    v_categoria_id BIGINT;
BEGIN
    SELECT id INTO v_categoria_id 
    FROM tb_categoria_gasto 
    WHERE UPPER(descricao) = 'TAXA CONDOMINIAL' 
    LIMIT 1;

    IF v_categoria_id IS NOT NULL THEN
        UPDATE tb_extrato
        SET status_conciliado = 1, -- 1 = BATIDO no EnumType.ORDINAL do JPA (0=PENDENTE, 1=BATIDO)
            id_categoria_gasto = COALESCE(id_categoria_gasto, v_categoria_id)
        WHERE tp_tranacao = 'BOLETO_COBRANCA' 
           OR id_boleto IS NOT NULL;
    END IF;
END $$;

-- Observação: Como o ambiente de execução não está conectado diretamente ao banco de dados,
-- este script pode ser executado manualmente via psql, DBeaver, pgAdmin ou em pipeline de migração.

