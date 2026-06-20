package com.condominio.novaalianca.banking.repositories;

import com.condominio.novaalianca.banking.models.entities.Saldo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SaldoRepository extends JpaRepository<Saldo, Long> {
    Optional<Saldo> findFirstByOrderByIdDesc();
}
