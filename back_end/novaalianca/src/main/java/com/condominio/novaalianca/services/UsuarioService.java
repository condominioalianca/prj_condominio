package com.condominio.novaalianca.services;

import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario usuarioSave(Usuario usuario){
      return usuarioRepository.save(usuario);
    }

    public  Usuario findById(Integer id){
        return usuarioRepository.findById(id).get();
    }

}
