package com.condominio.novaalianca.builder;

import com.condominio.novaalianca.dto.UnidadeDTO;
import com.condominio.novaalianca.entities.Unidade;
import com.condominio.novaalianca.repositories.UnidadeRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@Log4j2
public class UnidadeBuilder {

    @Autowired
    private UnidadeRepository unidadeRepository;

    public UnidadeDTO entityToDto(Unidade unidade){
        return  UnidadeDTO.builder()
                .idUnidade(unidade.getIdUnidade())
                .numeroUnidade(unidade.getNumeroUnidade())
                .andarUnidade(unidade.getAndarUnidade())
                .build();
    }


    public Unidade dtoToEntity(UnidadeDTO dto){

log.info("UNIDADE DTO TO ENTITY = {}", dto);
        Optional<Unidade> unidade = Objects.isNull(dto.getIdUnidade()) ? Optional.empty()  : unidadeRepository.findById(dto.getIdUnidade());
        return unidade.orElseGet(() -> Unidade.builder()
                .idUnidade(dto.getIdUnidade())
                .numeroUnidade(dto.getNumeroUnidade())
                .andarUnidade(dto.getAndarUnidade())
                .build());
    }
}
