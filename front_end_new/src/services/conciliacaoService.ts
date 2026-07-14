import api from './api';
import type {
  ConciliacaoResponseDTO,
  ExtratoConciliacaoPatchDTO,
  ExtratoResumoDTO,
  Page
} from '../types/conciliacao';

export const getConciliacoes = async (): Promise<ConciliacaoResponseDTO[]> => {
  const response = await api.get<ConciliacaoResponseDTO[]>('/conciliacao');
  return response.data;
};

export const getExtratosPaginado = async (
  conciliacaoId: number,
  page: number = 0,
  size: number = 20
): Promise<Page<ExtratoResumoDTO>> => {
  const response = await api.get<Page<ExtratoResumoDTO>>(`/conciliacao/${conciliacaoId}`, {
    params: {
      page,
      size,
    },
  });
  return response.data;
};

export const atualizarExtrato = async (
  extratoId: number,
  dto: ExtratoConciliacaoPatchDTO
): Promise<void> => {
  await api.patch(`/conciliacao/extrato/${extratoId}`, dto);
};

export const uploadComprovanteIndividual = async (extratoId: number, file: File): Promise<void> => {
  const formData = new FormData();
  formData.append('file', file);
  await api.post(`/comprovante/extrato/${extratoId}`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

export const uploadComprovanteLote = async (conciliacaoId: number, file: File): Promise<void> => {
  const formData = new FormData();
  formData.append('file', file);
  await api.post(`/comprovante/conciliacao/${conciliacaoId}`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

export const getComprovanteDownloadUrl = (idComprovante: number): string => {
  // Using the base URL from api config
  return `${api.defaults.baseURL}/comprovante/${idComprovante}`;
};

export const gerarPdf = async (conciliacaoId: number): Promise<void> => {
  await api.post(`/conciliacao/${conciliacaoId}/gerar-pdf`);
};

export const baixarPdf = async (conciliacaoId: number): Promise<void> => {
  const response = await api.get(`/conciliacao/${conciliacaoId}/pdf`, {
    responseType: 'blob',
  });
  const url = window.URL.createObjectURL(new Blob([response.data]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', `conciliacao_${conciliacaoId}.pdf`);
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};

