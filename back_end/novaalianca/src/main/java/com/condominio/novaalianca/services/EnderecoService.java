package com.condominio.novaalianca.services;

import com.condominio.novaalianca.builder.EnderecoBuilder;
import com.condominio.novaalianca.dto.EnderecoDTO;
import com.condominio.novaalianca.entities.Endereco;
import com.condominio.novaalianca.repositories.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service

public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private EnderecoBuilder enderecoBuilder;


    @Transactional
    public Page<EnderecoDTO> findAllPaged(Pageable pageable) {
        Page<Endereco> list = enderecoRepository.findAll(pageable);
        return list.map(x -> enderecoBuilder.entityToDto(x));
    }

    public EnderecoDTO findByID(Long id) {

        return enderecoBuilder.entityToDto(enderecoRepository.findById(id).get());
    }

    @Transactional
    public EnderecoDTO enderecoSave(EnderecoDTO dto) {
        Endereco endereco = enderecoBuilder.dtoToEntity(dto);
        endereco = enderecoRepository.save(endereco);

        return enderecoBuilder.entityToDto(endereco);
    }

    @Transactional
    public void deletById(Long idEndereco) {
        enderecoRepository.deleteById(idEndereco);
    }


}
