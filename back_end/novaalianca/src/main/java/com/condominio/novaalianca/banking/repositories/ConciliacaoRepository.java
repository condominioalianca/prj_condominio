package com.condominio.novaalianca.banking.repositories;

import com.condominio.novaalianca.banking.models.entities.Conciliacao;
import com.condominio.novaalianca.banking.models.enums.StatusConciliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import com.condominio.novaalianca.banking.models.dtos.ConciliacaoResponseDTO;


@Repository
public interface ConciliacaoRepository extends JpaRepository<Conciliacao, Long> {

    java.util.Optional<Conciliacao> findByDataReferencia(LocalDate dataReferencia);

    @Query("SELECT c FROM Conciliacao c WHERE " +
           "(:dataInicio IS NULL OR c.dataReferencia >= :dataInicio) AND " +
           "(:dataFim IS NULL OR c.dataReferencia <= :dataFim) AND " +
           "(:status IS NULL OR c.status = :status) " +
           "ORDER BY c.dataReferencia DESC")
    List<Conciliacao> findWithFilters(@Param("dataInicio") LocalDate dataInicio,
                                      @Param("dataFim") LocalDate dataFim,
                                      @Param("status") StatusConciliacao status);

    @Query("SELECT new com.condominio.novaalianca.banking.models.dtos.ConciliacaoResponseDTO(" +
           "c.id, c.descricao, " +
           "SUM(CASE WHEN e.statusConciliado = :statusBatido THEN 1L ELSE 0L END), " +
           "SUM(CASE WHEN e.statusConciliado = :statusPendente THEN 1L ELSE 0L END)) " +
           "FROM Conciliacao c LEFT JOIN c.extratos e " +
           "WHERE (:dataInicio IS NULL OR c.dataReferencia >= :dataInicio) AND " +
           "(:dataFim IS NULL OR c.dataReferencia <= :dataFim) AND " +
           "(:status IS NULL OR c.status = :status) " +
           "GROUP BY c.id, c.descricao, c.dataCriacao " +
           "ORDER BY c.dataReferencia DESC")
    List<ConciliacaoResponseDTO> findResumoWithFilters(@Param("dataInicio") LocalDate dataInicio,
                                                       @Param("dataFim") LocalDate dataFim,
                                                       @Param("status") StatusConciliacao status,
                                                       @Param("statusBatido") StatusConciliacao statusBatido,
                                                       @Param("statusPendente") StatusConciliacao statusPendente);
}
