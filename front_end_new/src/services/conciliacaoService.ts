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

export const baixarComprovante = async (idComprovante: number, nomeArquivoOriginal?: string | null): Promise<void> => {
  const response = await api.get(`/comprovante/${idComprovante}`, {
    responseType: 'blob',
  });

  let fileName = nomeArquivoOriginal || `comprovante_${idComprovante}.pdf`;
  const contentDisposition = response.headers['content-disposition'] as string | undefined;
  if (contentDisposition) {
    const fileNameMatch = contentDisposition.match(/filename="?([^"]+)"?/);
    if (fileNameMatch && fileNameMatch[1]) {
      fileName = fileNameMatch[1];
    }
  }

  const contentType = (response.headers['content-type'] as string) || 'application/pdf';
  const url = window.URL.createObjectURL(
    new Blob([response.data], { type: contentType })
  );
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', fileName);
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};

export const baixarComprovantePorConciliacao = async (conciliacaoId: number, nomeArquivoOriginal?: string | null): Promise<void> => {
  const response = await api.get(`/comprovante/conciliacao/${conciliacaoId}`, {
    responseType: 'blob',
  });

  let fileName = nomeArquivoOriginal || `comprovante_conciliacao_${conciliacaoId}.pdf`;
  const contentDisposition = response.headers['content-disposition'] as string | undefined;
  if (contentDisposition) {
    const fileNameMatch = contentDisposition.match(/filename="?([^"]+)"?/);
    if (fileNameMatch && fileNameMatch[1]) {
      fileName = fileNameMatch[1];
    }
  }

  const contentType = (response.headers['content-type'] as string) || 'application/pdf';
  const url = window.URL.createObjectURL(
    new Blob([response.data], { type: contentType })
  );
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', fileName);
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};

export const getComprovanteDownloadUrl = (idComprovante: number): string => {
  const token = localStorage.getItem('token');
  const tokenParam = token ? `?access_token=${encodeURIComponent(token)}` : '';
  return `${api.defaults.baseURL}/comprovante/${idComprovante}${tokenParam}`;
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

