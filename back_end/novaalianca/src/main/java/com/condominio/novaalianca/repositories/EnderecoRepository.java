package com.condominio.novaalianca.repositories;

import com.condominio.novaalianca.entities.Endereco;
import com.condominio.novaalianca.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

}
