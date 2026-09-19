package com.condominio.novaalianca.banking.repositories;

import com.condominio.novaalianca.banking.models.entities.CategoriaGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaGastoRepository extends JpaRepository<CategoriaGasto, Long> {
    List<CategoriaGasto> findByAtivoTrue();
    Optional<CategoriaGasto> findFirstByDescricaoIgnoreCase(String descricao);
    Optional<CategoriaGasto> findFirstByTipoAndDescricaoContainingIgnoreCase(String tipo, String descricao);
}

