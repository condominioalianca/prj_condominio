package com.condominio.novaalianca.builder;

import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.dto.UnidadeDTO;
import com.condominio.novaalianca.entities.Unidade;
import com.condominio.novaalianca.repositories.UnidadeRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UnidadeBuilder {

    private final UnidadeRepository unidadeRepository;

    public UnidadeDTO entityToDto(Unidade unidade){
        if (unidade == null) {
            return null;
        }
        return  UnidadeDTO.builder()
                .idUnidade(unidade.getIdUnidade())
                .numeroUnidade(unidade.getNumeroUnidade())
                .andarUnidade(unidade.getAndarUnidade())
                .build();
    }


    public Unidade dtoToEntity(UnidadeDTO dto){
        if (dto == null) {
            return null;
        }
        Optional<Unidade> unidade = Objects.isNull(dto.getIdUnidade()) ? Optional.empty()  : unidadeRepository.findById(dto.getIdUnidade());
        return unidade.orElseGet(() -> Unidade.builder()
                .idUnidade(dto.getIdUnidade())
                .numeroUnidade(dto.getNumeroUnidade())
                .andarUnidade(dto.getAndarUnidade())
                .build());
    }
}