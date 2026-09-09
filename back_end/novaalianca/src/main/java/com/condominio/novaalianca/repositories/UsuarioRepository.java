package com.condominio.novaalianca.repositories;

import com.condominio.novaalianca.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> , JpaSpecificationExecutor<Usuario> {

    @Query(value = "SELECT u FROM Usuario u WHERE u.enviaBoleto = true")
    List<Usuario> listUsuariosGeraBoleto();

    Usuario findByTxEmail(String email);

    Usuario findByNrDocumentoCpf(String nrCocumento);

    Usuario findByNrDocumentoCnpj(String nrDocumentoCnpj);

    @Query(value = "SELECT u FROM Usuario u WHERE u.ativo = true AND u.enviaBoleto = true")
    List<Usuario> findByAtivosAndEnviaBoleto();

    @Query(value = "SELECT u FROM Usuario u WHERE u.ativo = true AND u.enviaBoleto = true " +
            " AND u.idUsuario NOT IN ( SELECT b.usuario.idUsuario FROM BoletoNovaAlianca b WHERE b.dtEmissao BETWEEN :dtInicio AND :dtFim AND b.txSituacao NOT IN ('CANCELADO'))")
    List<Usuario> findFirstByAtivosAndEnviaBoletoAndSemBoleto(LocalDate dtInicio, LocalDate dtFim);


}