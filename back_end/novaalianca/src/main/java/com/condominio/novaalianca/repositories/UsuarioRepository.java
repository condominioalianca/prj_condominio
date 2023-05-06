package com.condominio.novaalianca.repositories;

import com.condominio.novaalianca.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

    @Query(value = "SELECT u FROM Usuario u WHERE u.enviaBoleto = true ")
    List<Usuario> listUsuariosGeraBoleto ();

    Usuario findByTxEmail(String email);
}
