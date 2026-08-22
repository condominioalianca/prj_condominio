package com.condominio.novaalianca.services;

import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.builder.EmpresaBuilder;
import com.condominio.novaalianca.dto.EmpresaDTO;
import com.condominio.novaalianca.entities.Empresa;
import com.condominio.novaalianca.repositories.EmpresaRepository;
import com.condominio.novaalianca.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository repository;

    private final EmpresaBuilder builder;

    @Transactional(readOnly = true)
    public List<EmpresaDTO> findAll() {
        List<Empresa> list = repository.findAll();
        return list.stream().map(builder::entityToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmpresaDTO findById(Long id) {
        Empresa entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada para o ID: " + id));
        return builder.entityToDto(entity);
    }

    @Transactional(readOnly = true)
    public EmpresaDTO findByNrDocumento(String nrDocumento) {
        Empresa entity = repository.findByNrDocumento(nrDocumento);
        if (entity == null) {
            throw new ResourceNotFoundException("Empresa nao encontrada para o documento: " + nrDocumento);
        }
        return builder.entityToDto(entity);
    }

    @Transactional
    public EmpresaDTO save(EmpresaDTO dto) {
        Empresa entity = builder.dtoToEntity(dto);
        entity = repository.save(entity);
        return builder.entityToDto(entity);
    }

    @Transactional
    public EmpresaDTO update(EmpresaDTO dto) {
        Empresa entity = repository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada para o ID: " + dto.getId()));
        
        entity.setNomeEmpresa(dto.getNomeEmpresa());
        entity.setNrDocumento(dto.getNrDocumento());
        entity.setNrCelular(dto.getNrCelular());
        entity.setNrTelefone(dto.getNrTelefone());
        entity.setTxEndereco(dto.getTxEndereco());
        entity.setTxEnderecoComplemento(dto.getTxEnderecoComplemento());
        entity.setTxEnderecoNumero(dto.getTxEnderecoNumero());
        entity.setTxCep(dto.getTxCep());
        entity.setTxBairro(dto.getTxBairro());
        entity.setTxEmail(dto.getTxEmail());
        
        entity = repository.save(entity);
        return builder.entityToDto(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Empresa nao encontrada para o ID: " + id);
        }
        repository.deleteById(id);
    }
}