package com.condominio.novaalianca.repositories;

import com.condominio.novaalianca.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Integer> {
}
