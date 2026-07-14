import api from './api';
import type { CategoriaGasto } from '../types/categoria';

export const getCategoriasAtivas = async (): Promise<CategoriaGasto[]> => {
  const response = await api.get<CategoriaGasto[]>('/categoria-gasto');
  return response.data;
};
