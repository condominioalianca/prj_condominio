package com.condominio.novaalianca.cobranca.builder;

import com.condominio.novaalianca.cobranca.models.dto.PagadorDTO;
import com.condominio.novaalianca.entities.BoletoNovaAlianca;
import org.springframework.stereotype.Component;

@Component
public class PagadorBuilder {

    public static PagadorDTO pagadorDTOBuilder (BoletoNovaAlianca boletoNovaAlianca){
        return PagadorDTO.builder()
                .cpfCnpj(boletoNovaAlianca.getUsuario().getNrDocumentoCpf())
                .nome(boletoNovaAlianca.getUsuario().getNomeUsuario())
                .email(boletoNovaAlianca.getUsuario().getTxEmail())
                .ddd(boletoNovaAlianca.getUsuario().getNrCelularDdd())
                .telefone(boletoNovaAlianca.getUsuario().getNrCelular())
                .tipoPessoa(boletoNovaAlianca.getUsuario().getTxTipoPessoa())
                .endereco(boletoNovaAlianca.getUsuario().getEndereco().getTxEndereco())
                .numero(boletoNovaAlianca.getUsuario().getEndereco().getTxEnderecoNumero())
                .complemento(boletoNovaAlianca.getUsuario().getEndereco().getTxEnderecoComplemento())
                .bairro(boletoNovaAlianca.getUsuario().getEndereco().getTxBairro())
                .cidade(boletoNovaAlianca.getUsuario().getEndereco().getTxCidade())
                .uf(boletoNovaAlianca.getUsuario().getEndereco().getTxUf())
                .cep(boletoNovaAlianca.getUsuario().getEndereco().getTxCep())
                .build();
    }
}
