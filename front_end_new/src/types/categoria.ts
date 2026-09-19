export interface CategoriaGasto {
  id: number;
  descricao: string;
  ativo: boolean;
  tipo?: 'C' | 'D' | string;
}
