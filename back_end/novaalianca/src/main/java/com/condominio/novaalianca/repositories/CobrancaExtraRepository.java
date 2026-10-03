package com.condominio.novaalianca.repositories;

import com.condominio.novaalianca.entities.CobrancaExtra;
import com.condominio.novaalianca.entities.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface CobrancaExtraRepository extends JpaRepository<CobrancaExtra,Long> {

    @Query(value = "Select ce FROM CobrancaExtra ce where ce.unidade = :uni AND ce.mesReferencia = :mesReferencia AND ce.anoReferencia = :anoReferencia")
    CobrancaExtra findByidUnidadeAndMesReferenciaAndAnoReferencia(Unidade uni, Long mesReferencia, Long anoReferencia);

    @Query(value = "SELECT ce FROM CobrancaExtra ce WHERE ce.recorrente = true AND " +
           "(ce.unidade = :uni OR ce.tipoAbrangencia = 'GERAL' OR ce.unidade IS NULL)")
    List<CobrancaExtra> findRecorrentesParaUnidade(@Param("uni") Unidade uni);

    @Query(value = "SELECT ce FROM CobrancaExtra ce WHERE (ce.recorrente = false OR ce.recorrente IS NULL) AND " +
           "(ce.unidade = :uni OR ce.tipoAbrangencia = 'GERAL' OR ce.unidade IS NULL) AND " +
           "ce.mesReferencia = :mesReferencia AND ce.anoReferencia = :anoReferencia")
    List<CobrancaExtra> findPontuaisParaUnidadeEMes(
            @Param("uni") Unidade uni,
            @Param("mesReferencia") Long mesReferencia,
            @Param("anoReferencia") Long anoReferencia);
}
