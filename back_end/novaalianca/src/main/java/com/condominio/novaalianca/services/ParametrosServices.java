package com.condominio.novaalianca.services;

import com.condominio.novaalianca.builder.ParametrosBuilder;
import com.condominio.novaalianca.dto.parametros.ParametrosDTO;
import com.condominio.novaalianca.entities.ParametrosSitema;
import com.condominio.novaalianca.repositories.ParametrosSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParametrosServices {

    @Autowired
    private ParametrosSistemaRepository repository;

    @Autowired
    private ParametrosBuilder builder;

    public List<ParametrosDTO> listParametros (){
        List<ParametrosSitema> listEntitesParametros = repository.findAll();
        return  listEntitesParametros.stream()
                .map( parametrosSitema -> builder.entityTODto(parametrosSitema))
                .collect(Collectors.toList());

    }

    public  ParametrosDTO findById (Long id){
        return builder.entityTODto(repository.findById(id).get());
    }

    public  ParametrosDTO save (ParametrosDTO parametrosDTO){
        ParametrosSitema entity = repository.save(builder.dtoToEntity(parametrosDTO));
        return builder.entityTODto(entity);
    }
}
