package com.condominio.novaalianca.builder;

import com.condominio.novaalianca.dto.EnderecoDTO;
import com.condominio.novaalianca.dto.PerfilDTO;
import com.condominio.novaalianca.entities.Endereco;
import com.condominio.novaalianca.entities.Perfil;
import org.springframework.stereotype.Component;

@Component
public class PerfilBuilder {

    public PerfilDTO entityToDto (Perfil entity){
        return PerfilDTO.builder()
                .id(entity.getId())
                .nomePerfil(entity.getNomePerfil())
                .build();
    }

    public Perfil dtoToEntity (PerfilDTO dto){
        return Perfil.builder()
                .id(dto.getId())
                .nomePerfil(dto.getNomePerfil())
                .build();
    }
}
