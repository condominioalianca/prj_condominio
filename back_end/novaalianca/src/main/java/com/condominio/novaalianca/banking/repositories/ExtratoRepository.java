package com.condominio.novaalianca.banking.repositories;

import com.condominio.novaalianca.banking.models.entities.Extrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.condominio.novaalianca.banking.models.dtos.ExtratoResumoDTO;

import java.util.Optional;

public interface ExtratoRepository extends JpaRepository<Extrato,Long> {
    @Query(value = "SELECT ex FROM Extrato ex Where ex.idTransacao = :idTransacao ")
    public Optional<Extrato> getbyIdTransacao(@Param("idTransacao") String idTransacao);

    @Query("SELECT new com.condominio.novaalianca.banking.models.dtos.ExtratoResumoDTO(" +
           "ex.id, ex.idTransacao, ex.dtInclusao, ex.dtTransacao, ex.descricao, " +
           "ex.tipoTransacao, ex.tipoOperacao, ex.tituloTransacao, ex.valorTransacao, " +
           "ex.nomeRecebedor, ex.documenteRecebedor, ex.nomePagador, ex.documentePagador, " +
           "ex.idBoleto, ex.statusConciliado, ex.statusGeral, " +
           "c.id, c.descricao, " +
           "CASE WHEN comp IS NOT NULL THEN true ELSE false END, " +
           "comp.id, comp.nomeArquivo) " +
           "FROM Extrato ex LEFT JOIN ex.categoriaGasto c LEFT JOIN ex.comprovante comp " +
           "WHERE ex.conciliacao.id = :conciliacaoId " +
           "ORDER BY ex.dtTransacao ASC")
    Page<ExtratoResumoDTO> findResumoByConciliacaoId(@Param("conciliacaoId") Long conciliacaoId, Pageable pageable);
}
