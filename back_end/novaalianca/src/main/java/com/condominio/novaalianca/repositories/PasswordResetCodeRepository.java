package com.condominio.novaalianca.repositories;

import com.condominio.novaalianca.entities.PasswordResetCode;
import com.condominio.novaalianca.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {
    Optional<PasswordResetCode> findFirstByUsuarioAndIsUsedFalseOrderByCreatedAtDesc(Usuario usuario);
    Optional<PasswordResetCode> findByCodeHashAndIsUsedFalse(String codeHash);
}
