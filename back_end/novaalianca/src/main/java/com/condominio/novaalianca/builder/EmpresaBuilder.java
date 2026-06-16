package com.condominio.novaalianca.builder;

import com.condominio.novaalianca.dto.EmpresaDTO;
import com.condominio.novaalianca.entities.Empresa;
import org.springframework.stereotype.Component;

@Component
public class EmpresaBuilder {

    public EmpresaDTO entityToDto(Empresa entity) {
        if (entity == null) return null;
        return EmpresaDTO.builder()
                .id(entity.getId())
                .nomeEmpresa(entity.getNomeEmpresa())
                .nrDocumento(entity.getNrDocumento())
                .nrCelular(entity.getNrCelular())
                .nrTelefone(entity.getNrTelefone())
                .txEndereco(entity.getTxEndereco())
                .txEnderecoComplemento(entity.getTxEnderecoComplemento())
                .txEnderecoNumero(entity.getTxEnderecoNumero())
                .txCep(entity.getTxCep())
                .txBairro(entity.getTxBairro())
                .txEmail(entity.getTxEmail())
                .build();
    }

    public Empresa dtoToEntity(EmpresaDTO dto) {
        if (dto == null) return null;
        return Empresa.builder()
                .id(dto.getId())
                .nomeEmpresa(dto.getNomeEmpresa())
                .nrDocumento(dto.getNrDocumento())
                .nrCelular(dto.getNrCelular())
                .nrTelefone(dto.getNrTelefone())
                .txEndereco(dto.getTxEndereco())
                .txEnderecoComplemento(dto.getTxEnderecoComplemento())
                .txEnderecoNumero(dto.getTxEnderecoNumero())
                .txCep(dto.getTxCep())
                .txBairro(dto.getTxBairro())
                .txEmail(dto.getTxEmail())
                .build();
    }
}
