import {Endereco} from "./endereco";

export type Usuario =  {

    idUsuario: number;
    nomeUsuario: string;
    txEmail: string;
     nrDocumentoCpf: string;
    // rgUsuario: string;
    nrCelularDdd: number;
    nrCelular: number;
    endereco : Endereco
}