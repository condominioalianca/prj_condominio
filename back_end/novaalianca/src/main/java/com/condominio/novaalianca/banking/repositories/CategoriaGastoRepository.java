package com.condominio.novaalianca.banking.repositories;

import com.condominio.novaalianca.banking.models.entities.CategoriaGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaGastoRepository extends JpaRepository<CategoriaGasto, Long> {
    List<CategoriaGasto> findByAtivoTrue();
}
