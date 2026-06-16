package com.condominio.novaalianca.builder;

import com.condominio.novaalianca.dto.ContaDTO;
import com.condominio.novaalianca.entities.Conta;
import com.condominio.novaalianca.entities.Empresa;
import org.springframework.stereotype.Component;

@Component
public class ContaBuilder {

    public ContaDTO entityToDto(Conta entity) {
        if (entity == null) return null;
        return ContaDTO.builder()
                .id(entity.getId())
                .codBanco(entity.getCodBanco())
                .nrAgencia(entity.getNrAgencia())
                .dgAgencia(entity.getDgAgencia())
                .nrConta(entity.getNrConta())
                .dgConta(entity.getDgConta())
                .idEmpresa(entity.getEmpresa() != null ? entity.getEmpresa().getId() : null)
                .build();
    }

    public Conta dtoToEntity(ContaDTO dto, Empresa empresa) {
        if (dto == null) return null;
        return Conta.builder()
                .id(dto.getId())
                .codBanco(dto.getCodBanco())
                .nrAgencia(dto.getNrAgencia())
                .dgAgencia(dto.getDgAgencia())
                .nrConta(dto.getNrConta())
                .dgConta(dto.getDgConta())
                .empresa(empresa)
                .build();
    }
}
