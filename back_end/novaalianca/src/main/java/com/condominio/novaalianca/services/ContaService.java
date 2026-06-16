package com.condominio.novaalianca.services;

import com.condominio.novaalianca.builder.ContaBuilder;
import com.condominio.novaalianca.dto.ContaDTO;
import com.condominio.novaalianca.entities.Conta;
import com.condominio.novaalianca.entities.Empresa;
import com.condominio.novaalianca.repositories.ContaRepository;
import com.condominio.novaalianca.repositories.EmpresaRepository;
import com.condominio.novaalianca.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContaService {

    @Autowired
    private ContaRepository repository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ContaBuilder builder;

    @Transactional(readOnly = true)
    public List<ContaDTO> findAll() {
        List<Conta> list = repository.findAll();
        return list.stream().map(builder::entityToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ContaDTO findById(Long id) {
        Conta entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta nao encontrada para o ID: " + id));
        return builder.entityToDto(entity);
    }

    @Transactional
    public ContaDTO save(ContaDTO dto) {
        Empresa empresa = null;
        if (dto.getIdEmpresa() != null) {
            empresa = empresaRepository.findById(dto.getIdEmpresa())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada para o ID: " + dto.getIdEmpresa()));
        }
        Conta entity = builder.dtoToEntity(dto, empresa);
        entity = repository.save(entity);
        return builder.entityToDto(entity);
    }

    @Transactional
    public ContaDTO update(ContaDTO dto) {
        Conta entity = repository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta nao encontrada para o ID: " + dto.getId()));
        
        Empresa empresa = null;
        if (dto.getIdEmpresa() != null) {
            empresa = empresaRepository.findById(dto.getIdEmpresa())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada para o ID: " + dto.getIdEmpresa()));
        }
        
        entity.setCodBanco(dto.getCodBanco());
        entity.setNrAgencia(dto.getNrAgencia());
        entity.setDgAgencia(dto.getDgAgencia());
        entity.setNrConta(dto.getNrConta());
        entity.setDgConta(dto.getDgConta());
        entity.setEmpresa(empresa);
        
        entity = repository.save(entity);
        return builder.entityToDto(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Conta nao encontrada para o ID: " + id);
        }
        repository.deleteById(id);
    }
}
