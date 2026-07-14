export type StatusConciliacao = 'PENDENTE' | 'BATIDO';

export type StatusGeral = 'ATIVO' | 'INATIVO';

export interface ConciliacaoResponseDTO {
  id: number;
  descricao: string;
  status: StatusConciliacao;
  qtdBatido: number;
  qtdDivergente: number;
  qtdTotal: number;
}

export interface ExtratoResumoDTO {
  id: number;
  idTransacao: string;
  dtInclusao: string;
  dtTransacao: string;
  descricao: string | null;
  tipoTransacao: string;
  tipoOperacao: string;
  tituloTransacao: string;
  valorTransacao: number;
  nomeRecebedor: string | null;
  documenteRecebedor: string | null;
  nomePagador: string | null;
  documentePagador: string | null;
  idBoleto: number | null;
  statusConciliado: StatusConciliacao;
  statusGeral: StatusGeral;
  idCategoriaGasto: number | null;
  descricaoCategoriaGasto: string | null;
  possuiComprovante: boolean;
  idComprovante: number | null;
  nomeArquivoComprovante: string | null;
}

export interface ExtratoConciliacaoPatchDTO {
  descricao?: string;
  idCategoriaGasto?: number;
  statusConciliado?: StatusConciliacao;
}

export interface Page<T> {
  content: T[];
  pageable: {
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    pageNumber: number;
    pageSize: number;
    paged: boolean;
    unpaged: boolean;
  };
  last: boolean;
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}
