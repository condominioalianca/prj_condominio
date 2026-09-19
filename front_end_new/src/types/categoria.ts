export interface CategoriaGasto {
  id: number;
  descricao: string;
  ativo: boolean;
  tipo?: 'C' | 'D' | string;
}

export const getTipoCategoria = (cat: CategoriaGasto): 'C' | 'D' => {
  if (cat.tipo) {
    const t = cat.tipo.toUpperCase().trim();
    if (t === 'C' || t === 'CREDITO') return 'C';
    if (t === 'D' || t === 'DEBITO') return 'D';
  }

  const desc = (cat.descricao || '').toLowerCase();
  const termosCredito = [
    'taxa condominial',
    'condominial',
    'multa',
    'juros',
    'rendimento',
    'aplicação',
    'aplicacao',
    'receita',
    'fundo de reserva',
    'mudança',
    'mudanca',
    'espaço',
    'espaco',
  ];

  if (termosCredito.some((termo) => desc.includes(termo))) {
    return 'C';
  }

  return 'D';
};

