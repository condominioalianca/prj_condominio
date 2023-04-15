package com.condominio.novaalianca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnderecoDTO {

    private Long idEndereco;

    private String txEndereco;

    private String txEnderecoNumero;

    private String txEnderecoComplemento;

    private String txBairro;

    private String txCidade;

    private String txUf;

    private String txCep;


}
