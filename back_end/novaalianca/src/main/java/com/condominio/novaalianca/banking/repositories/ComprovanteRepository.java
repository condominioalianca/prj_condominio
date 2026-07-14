package com.condominio.novaalianca.banking.repositories;

import com.condominio.novaalianca.banking.models.entities.Comprovante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComprovanteRepository extends JpaRepository<Comprovante, Long> {
}
