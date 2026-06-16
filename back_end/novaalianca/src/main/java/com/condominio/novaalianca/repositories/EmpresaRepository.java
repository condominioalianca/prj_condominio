package com.condominio.novaalianca.repositories;

import com.condominio.novaalianca.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Empresa findByNrDocumento(String nrDocumento);
}
