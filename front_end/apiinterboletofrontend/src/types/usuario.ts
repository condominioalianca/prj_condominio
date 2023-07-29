import {Endereco} from "./endereco";
import {Unidade} from "./unidade";

export type Usuario = {

    id: number;
    nomeUsuario: string;
    email: string;
    cpf: string;
    nrCelularDdd: number;
    nrCelular: number;
    enviaBoleto: boolean;
    enviaSms: boolean;
    ativo: boolean;
    endereco: Endereco;

    unidade: Unidade;

    tipoPessoa: string;
}