package com.condominio.novaalianca.services;

import com.condominio.novaalianca.entities.EnderecoUsuario;
import com.condominio.novaalianca.repositories.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    public void salvarEndereco (EnderecoUsuario endereco){
    enderecoRepository.save(endereco);
    }
}
