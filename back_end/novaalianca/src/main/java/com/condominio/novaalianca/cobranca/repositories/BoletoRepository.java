package com.condominio.novaalianca.cobranca.repositories;

import com.condominio.novaalianca.entities.BoletoNovaAlianca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BoletoRepository extends JpaRepository<BoletoNovaAlianca, Long> {
    BoletoNovaAlianca findByNossoNumero(String nossoNumero);

    @Query(value = "select b From BoletoNovaAlianca b join Usuario u on u.idUsuario = b.usuario.idUsuario where u.nrDocumentoCpf =:cpfUsuario")
    Page<BoletoNovaAlianca> findAllbyCpfUsuario(Pageable pageable, String cpfUsuario);

    @Query(value = "select b From BoletoNovaAlianca b join fetch Usuario u on u.idUsuario = b.usuario.idUsuario where u.idUsuario =:idUsuario")
    Page<BoletoNovaAlianca> findAllbyIdUsuario(Pageable pageable, Long idUsuario);

    @Query(value = "select b From BoletoNovaAlianca b where TO_CHAR(b.dtEmissao, 'MM') =:mesEmissao AND b.ativo=TRUE")
    List<BoletoNovaAlianca> findAllByMesEmissao(String mesEmissao);


    @Query(value = "select b From BoletoNovaAlianca b where date_trunc('MONTH',b.dtEmissao) BETWEEN to_date(:dataInicial, 'YYYY/MM/DD') AND to_date(:dataFinal, 'YYYY/MM/DD') AND b.ativo=TRUE AND b.emailEnviado = FALSE")
    List<BoletoNovaAlianca> findAllByMesEmissaoAndNaoEnviadoByEmail(LocalDate dataInicial, LocalDate dataFinal);

    @Query(value = "select b From BoletoNovaAlianca b where date_trunc('MONTH',b.dtEmissao) BETWEEN to_date(:dataInicial, 'YYYY/MM/DD') AND to_date(:dataFinal, 'YYYY/MM/DD')")
    List<BoletoNovaAlianca> findAllByDateFiltro(LocalDate dataInicial, LocalDate dataFinal);

    BoletoNovaAlianca findByTxCodBarras(String codigoBarras);

    BoletoNovaAlianca findByCodSolicitacao(String codSolicitacao);

    @Query("select b from BoletoNovaAlianca b where (b.txCodBarras is null or b.txCodBarras = '' or b.txLinhaDigitavel is null or b.txLinhaDigitavel = '' or b.arquivopdf is null) and b.codSolicitacao is not null and b.codSolicitacao <> '' and b.dtEmissao > ?1")
    List<BoletoNovaAlianca> findBoletosSemCodigoBarrasELinhaDigitavel(LocalDate dataCorte);

    @Query("select b from BoletoNovaAlianca b where b.codSolicitacao is not null and b.codSolicitacao <> '' and b.dtEmissao >= ?1 and (b.txSituacao is null or b.txSituacao <> 'RECEBIDO')")
    List<BoletoNovaAlianca> findBoletosParaEnriquecer(LocalDate dataCorte);
}
