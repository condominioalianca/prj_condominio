package com.condominio.novaalianca.services;

import com.condominio.novaalianca.builder.PerfilBuilder;
import com.condominio.novaalianca.dto.PerfilDTO;
import com.condominio.novaalianca.entities.Perfil;
import com.condominio.novaalianca.repositories.PerfilRepository;
import com.condominio.novaalianca.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PerfilService {

    @Autowired
    private PerfilRepository repository;

    @Autowired
    private PerfilBuilder builder;

    @Transactional(readOnly = true)
    public List<PerfilDTO> findAll() {
        List<Perfil> list = repository.findAll();
        return list.stream().map(builder::entityToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PerfilDTO findById(Long id) {
        Perfil entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil nao encontrado para o ID: " + id));
        return builder.entityToDto(entity);
    }

    @Transactional
    public PerfilDTO save(PerfilDTO dto) {
        Perfil entity = builder.dtoToEntity(dto);
        entity = repository.save(entity);
        return builder.entityToDto(entity);
    }

    @Transactional
    public PerfilDTO update(PerfilDTO dto) {
        Perfil entity = repository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil nao encontrado para o ID: " + dto.getId()));
        
        entity.setNomePerfil(dto.getNomePerfil());
        
        entity = repository.save(entity);
        return builder.entityToDto(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Perfil nao encontrado para o ID: " + id);
        }
        repository.deleteById(id);
    }
}
