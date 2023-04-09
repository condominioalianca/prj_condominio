package com.condominio.novaalianca.repositories;

import com.condominio.novaalianca.entities.EnderecoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoRepository extends JpaRepository<EnderecoUsuario, Integer> {
}
