package com.condominio.novaalianca.builder;

import com.condominio.novaalianca.dto.EnderecoDTO;
import com.condominio.novaalianca.entities.Endereco;
import org.springframework.stereotype.Component;

@Component
public class EnderecoBuilder {

    public EnderecoDTO entityToDto (Endereco endereco){
        return EnderecoDTO.builder()
                .idEndereco(endereco.getIdEndereco())
                .txEndereco(endereco.getTxEndereco())
                .txEnderecoNumero(endereco.getTxEnderecoNumero())
                .txEnderecoComplemento(endereco.getTxEnderecoComplemento())
                .txBairro(endereco.getTxBairro())
                .txCidade(endereco.getTxCidade())
                .txUf(endereco.getTxUf())
                .txCep(endereco.getTxCep())
                .build();
    }

    public Endereco dtoToEntity(EnderecoDTO dto) {

        return Endereco.builder()
                .idEndereco(dto.getIdEndereco())
                .txEndereco(dto.getTxEndereco())
                .txEnderecoNumero(dto.getTxEnderecoNumero())
                .txEnderecoComplemento(dto.getTxEnderecoComplemento())
                .txBairro(dto.getTxBairro())
                .txCidade(dto.getTxCidade())
                .txUf(dto.getTxUf())
                .txCep(dto.getTxCep())
                .build();
    }
}
