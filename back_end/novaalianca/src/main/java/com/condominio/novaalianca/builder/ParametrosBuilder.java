package com.condominio.novaalianca.builder;

import com.condominio.novaalianca.dto.parametros.ParametrosDTO;
import com.condominio.novaalianca.entities.ParametrosSitema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ParametrosBuilder {

    @Autowired
    private UsuarioBuilder usuarioBuilder;

    public ParametrosDTO entityTODto(ParametrosSitema entity){
        return ParametrosDTO.builder()
                .idParametro(entity.getIdParametro())
                .valorParametro(entity.getValorParametro())
                .descParametro(entity.getDescParametro())
                .dtCriacao(entity.getDtCriacao())
                .dtAlteracao(entity.getDtAlteracao())
                .usuarioCriacao(usuarioBuilder.entityToDto(entity.getUsuarioCriacao()))
                .usuarioAlteracao(Objects.isNull(entity.getUsuarioAlteracao()) ? null : usuarioBuilder.entityToDto(entity.getUsuarioAlteracao()))
                .ativo(entity.getAtivo())
                .build();
    }

    public ParametrosSitema dtoToEntity(ParametrosDTO dto){
        return ParametrosSitema.builder()
                .idParametro(dto.getIdParametro())
                .valorParametro(dto.getValorParametro())
                .descParametro(dto.getDescParametro())
                .dtCriacao(dto.getDtCriacao())
                .dtAlteracao(dto.getDtAlteracao())
                .usuarioCriacao(usuarioBuilder.dtoToEntity(dto.getUsuarioCriacao()))
                .usuarioAlteracao(Objects.isNull(dto.getUsuarioAlteracao()) ? null : usuarioBuilder.dtoToEntity(dto.getUsuarioAlteracao()))
                .ativo(dto.getAtivo())
                .build();
    }
}
