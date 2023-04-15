package com.condominio.novaalianca.services;

import com.condominio.novaalianca.builder.UsuarioBuilder;
import com.condominio.novaalianca.dto.UsuarioDTO;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.EnderecoRepository;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private UsuarioBuilder usuarioBuilder;




    @Transactional
    public Page<UsuarioDTO> findAllPaged(Pageable pageable) {
        Page<Usuario> list = usuarioRepository.findAll(pageable);
        return list.map( x -> usuarioBuilder.entityToDto(x));
    }

    public UsuarioDTO findByID(Long id) {
        return usuarioBuilder.entityToDto(usuarioRepository.findById(id).get());
    }

    @Transactional
    public UsuarioDTO usuarioSave(UsuarioDTO dto){
        Usuario usuario = usuarioBuilder.dtoToEntity(dto);
        usuario = usuarioRepository.save(usuario);

        return usuarioBuilder.entityToDto(usuario);
    }

    @Transactional
    public void deletById(Long idUsuario) {

        usuarioRepository.deleteById(idUsuario);
    }


}
