package com.condominio.novaalianca.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UsuarioDTO {


    private Long idUsuario;

    private String nomeUsuario;

    private String txEmail;

    private String nrTelefoneDdd;

    private String nrTelefone;

    private String nrCelularDdd;

    private String nrCelular;

    private String nrDocumentoCpf;

    private String nrDocumentoCnpj;

    private String txTipoPessoa;

    private boolean enviaBoleto;

    private boolean enviaSms;


    private boolean ativo;


    private EnderecoDTO enderecoDTO;


    private UnidadeDTO unidadeDTO;

}
