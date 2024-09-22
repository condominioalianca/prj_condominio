package com.condominio.novaalianca.banking.repositories;

import com.condominio.novaalianca.banking.models.entities.Extrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ExtratoRepository extends JpaRepository<Extrato,Long> {
    @Query(value = "SELECT ex FROM Extrato ex Where ex.idTransacao = :idTransacao ")
    public Optional<Extrato> getbyIdTransacao(String idTransacao);
}
