package com.condominio.novaalianca.job.repositories;

import com.condominio.novaalianca.entities.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnidadeRepository extends JpaRepository<Unidade, Long> {

    Unidade findByNumeroUnidade(String numeroUnidade);
}
