package com.condominio.novaalianca.job.repositories;

import com.condominio.novaalianca.entities.Boleto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    Boleto findByNossoNumero(String nossoNumero);

    @Query(value = "select b From Boleto b join Usuario u on u.idUsuario = b.usuario.idUsuario where u.nrDocumentoCpf =:cpfUsuario")
    Page<Boleto> findAllbyCpfUsuario(Pageable pageable, String cpfUsuario);

    @Query(value = "select b From Boleto b join fetch Usuario u on u.idUsuario = b.usuario.idUsuario where u.idUsuario =:idUsuario")
    Page<Boleto> findAllbyIdUsuario(Pageable pageable, Long idUsuario);
}
