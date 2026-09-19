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

-- Observação: Como o ambiente de execução não está conectado diretamente ao banco de dados,
-- este script pode ser executado manualmente via psql, DBeaver, pgAdmin ou em pipeline de migração.
