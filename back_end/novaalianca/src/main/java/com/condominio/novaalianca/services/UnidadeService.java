package com.condominio.novaalianca.services;

import com.condominio.novaalianca.builder.UnidadeBuilder;
import com.condominio.novaalianca.builder.UsuarioBuilder;
import com.condominio.novaalianca.dto.UnidadeDTO;
import com.condominio.novaalianca.dto.UsuarioDTO;
import com.condominio.novaalianca.entities.Unidade;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.EnderecoRepository;
import com.condominio.novaalianca.repositories.UnidadeRepository;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service

public class UnidadeService {

    @Autowired
    private UnidadeRepository repository;

    @Autowired
    private UnidadeBuilder builder;

    @Transactional
    public Page<UnidadeDTO> findAllPaged(Pageable pageable) {
        Page<Unidade> list = repository.findAll(pageable);
        return list.map( x -> builder.entityToDto(x));
    }

    public UnidadeDTO findByID(Long id) {
        return builder.entityToDto(repository.findById(id).get());
    }

    @Transactional
    public UnidadeDTO unidadeSave(UnidadeDTO dto){
        Unidade unidade = builder.dtoToEntity(dto);
        unidade = repository.save(unidade);

        return builder.entityToDto(unidade);
    }

    @Transactional
    public void deletById(Long idUnidade) {

        repository.deleteById(idUnidade);
    }


}
