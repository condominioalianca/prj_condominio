export interface IPerfil {
  id: number;
  nomePerfil: string; // 'ADMINISTRADOR' | 'SINDICO' | 'USUARIO'
}

export interface IUnidade {
  idUnidade: number;
  numeroUnidade: string;
  andarUnidade: string;
}

export interface IEndereco {
  idEndereco: number | null;
  txEndereco: string;
  txEnderecoNumero: string;
  txEnderecoComplemento: string;
  txBairro: string;
  txCidade: string;
  txUf: string;
  txCep: string;
}

export interface IUsuario {
  id: number | null;
  nomeUsuario: string;
  email: string;
  password?: string;
  nrTelefoneDdd: string | null;
  nrTelefone: string | null;
  nrCelularDdd: string | null;
  nrCelular: string | null;
  cpf: string | null;
  nrDocumentoCnpj: string | null;
  tipoPessoa: string; // 'F' | 'J'
  enviaBoleto: boolean;
  enviaSms: boolean;
  ativo: boolean;
  unidade: IUnidade | null;
  endereco: IEndereco | null;
  listPerfis: IPerfil[];
}

export interface IEmpresa {
  id: number | null;
  nomeEmpresa: string;
  nrDocumento: string;
  nrCelular: string | null;
  nrTelefone: string | null;
  txEndereco: string | null;
  txEnderecoComplemento: string | null;
  txEnderecoNumero: string | null;
  txCep: string | null;
  txBairro: string | null;
  txEmail: string | null;
}

export interface IPixDetalhe {
  id?: number;
  txCpfCnpj?: string;
  txNome?: string;
}

export interface IPagamentoDetalhe {
  id?: number;
  txCpfCnpj?: string;
  txNome?: string;
}

export interface ICompraDebitoDetalhe {
  id?: number;
  txCpfCnpj?: string;
  txNome?: string;
}

export interface IExtrato {
  id: number;
  idTransacao: string;
  dtInclusao: string; // ISO date string (YYYY-MM-DD)
  dtTransacao: string | null;
  descricao: string;
  tipoTransacao: string; // 'PIX', 'DEBITO_AUTOMATICO', 'BOLETO_COBRANCA', 'PAGAMENTO', etc.
  tipoOperacao: 'D' | 'C'; // 'D' = Debito, 'C' = Credito
  tituloTransacao: string;
  valorTransacao: number;
  nomeRecebedor: string | null;
  documentoRecebedor?: string | null;
  nomePagador: string | null;
  documentoPagador?: string | null;
  idBoleto: number | null;
  pixDetalhe?: IPixDetalhe | null;
  pagamentoDetalhe?: IPagamentoDetalhe | null;
  compraDebitoDetalhe?: ICompraDebitoDetalhe | null;
}

export interface IBoleto {
  id: number;
  nossoNumero: string;
  seuNumero: string;
  txCancelamento: string | null;
  txSituacao: string; // 'PAGO' | 'ABERTO' | 'VENCIDO' | 'CANCELADO' etc.
  dhSituacao: string | null;
  dtVencimento: string; // YYYY-MM-DD
  valor: number;
  dtEmissao: string; // YYYY-MM-DD
  dtLimitePagamento: string | null;
  txEspecie: string | null;
  txCodBarras: string | null;
  txLinhaDigitavel: string | null;
  txOrigem: string | null;
  empresa: IEmpresa | null;
  usuario: IUsuario | null;
  valorPagamento: number | null;
  motivoBaixa: string | null;
  dtBaixa: string | null;
  dtPagamento: string | null;
  arquivopdf: string | null; // Base64 PDF string
  ativo: boolean;
  emailEnviado: boolean;
  codSolicitacao: string | null;
}

export interface ICobrancaExtra {
  idCobrancaExtra: number | null;
  valorCobranca: number;
  dtInclusao: string | null;
  mesReferencia: number;
  descricao: string;
  idUnidade: number;
}

export interface IParametro {
  idParametro: number | null;
  descParametro: string;
  valorParametro: string;
  dtCriacao: string | null;
  dtAlteracao: string | null;
  usuarioCriacao: IUsuario | null;
  usuarioAlteracao: IUsuario | null;
  ativo: boolean;
}

export interface IAuthResponse {
  access_token: string;
  token_type: string;
  expires_in: number;
  scope: string;
  userName: string;
  userId: number;
  roles: string[];
}

export interface IUserSession {
  userId: number;
  userName: string;
  email: string;
  roles: string[];
  token: string;
}

// Interfaces de resposta paginada do Spring Boot Pageable
export interface ISprungPage<T> {
  content: T[];
  pageable: {
    sort: {
      sorted: boolean;
      unsorted: boolean;
      empty: boolean;
    };
    pageNumber: number;
    pageSize: number;
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  numberOfElements: number;
  size: number;
  number: number;
  sort: {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  };
  first: boolean;
  empty: boolean;
}
