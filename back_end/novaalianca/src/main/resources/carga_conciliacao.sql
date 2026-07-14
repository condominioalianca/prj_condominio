-- Script de carga inicial para a Conciliação Bancária
-- Cria as conciliações de Janeiro de 2026 até Julho de 2026 (mês atual)
-- Note: O status 1 = PENDENTE, status_geral 1 = ATIVO

INSERT INTO tb_conciliacao (dt_referencia, descricao, status, status_geral, dt_criacao, usuario_criacao)
VALUES 
('2026-01-01', 'Conciliação Janeiro 2026', 1, 1, CURRENT_TIMESTAMP, 'script_carga'),
('2026-02-01', 'Conciliação Fevereiro 2026', 1, 1, CURRENT_TIMESTAMP, 'script_carga'),
('2026-03-01', 'Conciliação Março 2026', 1, 1, CURRENT_TIMESTAMP, 'script_carga'),
('2026-04-01', 'Conciliação Abril 2026', 1, 1, CURRENT_TIMESTAMP, 'script_carga'),
('2026-05-01', 'Conciliação Maio 2026', 1, 1, CURRENT_TIMESTAMP, 'script_carga'),
('2026-06-01', 'Conciliação Junho 2026', 1, 1, CURRENT_TIMESTAMP, 'script_carga'),
('2026-07-01', 'Conciliação Julho 2026', 1, 1, CURRENT_TIMESTAMP, 'script_carga');

-- Atualiza a tabela tb_extrato vinculando os extratos às suas respectivas conciliações
-- Também inicializa os status para os registros antigos
UPDATE tb_extrato
SET conciliacao_id = c.id_conciliacao,
    status_conciliado = 1,
    status_geral = 1
FROM tb_conciliacao c
WHERE EXTRACT(MONTH FROM tb_extrato.dt_transacao) = EXTRACT(MONTH FROM c.dt_referencia)
  AND EXTRACT(YEAR FROM tb_extrato.dt_transacao) = EXTRACT(YEAR FROM c.dt_referencia);

-- Carga inicial das Categorias de Gasto
INSERT INTO tb_categoria_gasto (descricao, ativo) VALUES
('Água e Esgoto', true),
('Energia Elétrica', true),
('Manutenção e Reparos', true),
('Limpeza e Conservação', true),
('Honorários Contábeis', true),
('Tarifas Bancárias', true),
('Material de Consumo', true),
('Folha de Pagamento', true),
('Seguro do Condomínio', true),
('Outras Despesas', true);
