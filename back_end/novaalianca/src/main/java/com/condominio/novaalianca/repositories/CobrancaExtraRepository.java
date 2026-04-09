package com.condominio.novaalianca.repositories;

import com.condominio.novaalianca.entities.CobrancaExtra;
import com.condominio.novaalianca.entities.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CobrancaExtraRepository extends JpaRepository<CobrancaExtra,Long> {

    @Query(value = "Select ce FROM CobrancaExtra ce where ce.unidade = :uni AND ce.mesReferencia = :mesReferencia ")
    CobrancaExtra findByidUnidadeAndMesReferencia(Unidade uni, Integer mesReferencia);
}
