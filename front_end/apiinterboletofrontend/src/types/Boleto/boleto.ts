import {Desconto} from "./desconto";
import {Multa} from "./multa";
import {Mora} from "./mora";
import {Pagador} from "./pagador";
import {Unidade} from "../unidade";
import {Usuario} from "../usuario";


export type Boleto = {


    id: number;

    nossoNumero: string;

    seuNumero: string;

    txCancelamento: string;

    txSituacao: string;

    dhSituacao: string;

    dtVencimento: string;

    valor: number;

    dtEmissao: string;

    dtLimitePagamento: string;

    txEspecie: string;

    txCodBarras: string;

    txLinhaDigitavel: string;

    txOrigem: string;

    usuario: Usuario;

    valorPagamento: number;

    motivoBaixa: string;

    dtBaixa: string;

    dtPagamento: string;

    dtEnvio: string;

    mesReferencia: string;

    anoReferencia: number;

    unidade: Unidade;

    ativo: boolean;
}