package com.condominio.novaalianca.builder;

import com.condominio.novaalianca.dto.CobrancaExtraDTO;
import com.condominio.novaalianca.entities.CobrancaExtra;
import com.condominio.novaalianca.entities.Unidade;
import org.springframework.stereotype.Component;

@Component
public class CobrancaExtraBuilder {

    public CobrancaExtraDTO entityToDto(CobrancaExtra entity) {
        if (entity == null) return null;
        return CobrancaExtraDTO.builder()
                .idCobrancaExtra(entity.getIdCobrancaExtra())
                .valorCobranca(entity.getValorCobranca())
                .dtInclusao(entity.getDtInclusao())
                .mesReferencia(entity.getMesReferencia())
                .descricao(entity.getDescricao())
                .idUnidade(entity.getUnidade() != null ? entity.getUnidade().getIdUnidade() : null)
                .build();
    }

    public CobrancaExtra dtoToEntity(CobrancaExtraDTO dto, Unidade unidade) {
        if (dto == null) return null;
        return CobrancaExtra.builder()
                .idCobrancaExtra(dto.getIdCobrancaExtra())
                .valorCobranca(dto.getValorCobranca())
                .dtInclusao(dto.getDtInclusao())
                .mesReferencia(dto.getMesReferencia())
                .descricao(dto.getDescricao())
                .unidade(unidade)
                .build();
    }
}
