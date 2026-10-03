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
                .anoReferencia(entity.getAnoReferencia())
                .descricao(entity.getDescricao())
                .idUnidade(entity.getUnidade() != null ? entity.getUnidade().getIdUnidade() : null)
                .tipoOperacao(entity.getTipoOperacao() != null ? entity.getTipoOperacao() : "ACRESCIMO")
                .recorrente(entity.getRecorrente() != null ? entity.getRecorrente() : false)
                .tipoAbrangencia(entity.getTipoAbrangencia() != null ? entity.getTipoAbrangencia() : (entity.getUnidade() != null ? "UNIDADE" : "GERAL"))
                .build();
    }

    public CobrancaExtra dtoToEntity(CobrancaExtraDTO dto, Unidade unidade) {
        if (dto == null) return null;
        String tipoAbrangencia = dto.getTipoAbrangencia() != null ? dto.getTipoAbrangencia() : (unidade != null ? "UNIDADE" : "GERAL");
        boolean isRecorrente = Boolean.TRUE.equals(dto.getRecorrente());
        return CobrancaExtra.builder()
                .idCobrancaExtra(dto.getIdCobrancaExtra())
                .valorCobranca(dto.getValorCobranca())
                .dtInclusao(dto.getDtInclusao())
                .mesReferencia(isRecorrente ? null : dto.getMesReferencia())
                .anoReferencia(isRecorrente ? null : dto.getAnoReferencia())
                .descricao(dto.getDescricao())
                .tipoOperacao(dto.getTipoOperacao() != null ? dto.getTipoOperacao() : "ACRESCIMO")
                .recorrente(isRecorrente)
                .tipoAbrangencia(tipoAbrangencia)
                .unidade("GERAL".equals(tipoAbrangencia) ? null : unidade)
                .build();
    }
}
