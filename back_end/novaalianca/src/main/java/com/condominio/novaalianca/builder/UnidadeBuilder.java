package com.condominio.novaalianca.builder;

import com.condominio.novaalianca.dto.UnidadeDTO;
import com.condominio.novaalianca.entities.Unidade;
import org.springframework.stereotype.Component;

@Component
public class UnidadeBuilder {

    public UnidadeDTO entityToDto(Unidade unidade){
        return  UnidadeDTO.builder()
                .idUnidade(unidade.getIdUnidade())
                .numeroUnidade(unidade.getNumeroUnidade())
                .andarUnidade(unidade.getAndarUnidade())
                .build();
    }


    public Unidade dtoToEntity(UnidadeDTO dto){
        return  Unidade.builder()
                .idUnidade(dto.getIdUnidade())
                .numeroUnidade(dto.getNumeroUnidade())
                .andarUnidade(dto.getAndarUnidade())
                .build();
    }
}
