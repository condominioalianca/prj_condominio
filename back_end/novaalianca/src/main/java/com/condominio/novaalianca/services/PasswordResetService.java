package com.condominio.novaalianca.services;

import com.condominio.novaalianca.entities.PasswordResetCode;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.PasswordResetCodeRepository;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import com.condominio.novaalianca.services.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetCodeRepository codeRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void requestPasswordReset(String email) {
        log.info("Iniciando solicitação de reset de senha para o email: {}", email);
        Usuario usuario = usuarioRepository.findByTxEmail(email);
        if (usuario == null) {
            // Em termos de segurança de enumeração de e-mail, poderíamos ignorar silenciosamente.
            // Para este sistema do condomínio, lançaremos erro para feedback claro do usuário.
            throw new ResourceNotFoundException("Usuário com o e-mail informado não foi encontrado.");
        }

        // Inutilizar qualquer código anterior ativo para este usuário
        Optional<PasswordResetCode> activeCodeOpt = codeRepository.findFirstByUsuarioAndIsUsedFalseOrderByCreatedAtDesc(usuario);
        activeCodeOpt.ifPresent(code -> {
            code.setUsed(true);
            codeRepository.save(code);
        });

        // Gerar código seguro de 8 dígitos
        String rawCode = generateSecureCode();
        String codeHash = hashSha256(rawCode);

        PasswordResetCode resetCode = PasswordResetCode.builder()
                .usuario(usuario)
                .codeHash(codeHash)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .attempts(0)
                .isUsed(false)
                .createdAt(LocalDateTime.now())
                .build();

        codeRepository.save(resetCode);

        // Enviar o e-mail com o código em texto plano
        try {
            emailService.sendPasswordResetEmail(usuario.getTxEmail(), rawCode);
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de recuperação de senha: {}", e.getMessage());
            throw new RuntimeException("Não foi possível enviar o e-mail com o código de segurança. Tente novamente.");
        }
    }

    @Transactional
    public void confirmPasswordReset(String email, String code, String newPassword) {
        log.info("Confirmando redefinição de senha para o email: {}", email);
        Usuario usuario = usuarioRepository.findByTxEmail(email);
        if (usuario == null) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }

        PasswordResetCode resetCode = codeRepository.findFirstByUsuarioAndIsUsedFalseOrderByCreatedAtDesc(usuario)
                .orElseThrow(() -> new IllegalArgumentException("Código de redefinição não encontrado ou já utilizado."));

        // Validar Expiração
        if (resetCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            resetCode.setUsed(true);
            codeRepository.save(resetCode);
            throw new IllegalArgumentException("O código de segurança expirou. Solicite um novo código.");
        }

        // Validar limite de tentativas (bloqueio de brute-force)
        if (resetCode.getAttempts() >= 3) {
            resetCode.setUsed(true);
            codeRepository.save(resetCode);
            throw new IllegalArgumentException("Limite de tentativas excedido. Solicite um novo código.");
        }

        // Comparar Hash do código inserido
        String inputHash = hashSha256(code.trim());
        if (!resetCode.getCodeHash().equals(inputHash)) {
            resetCode.setAttempts(resetCode.getAttempts() + 1);
            if (resetCode.getAttempts() >= 3) {
                resetCode.setUsed(true);
                codeRepository.save(resetCode);
                throw new IllegalArgumentException("Código incorreto. Limite de tentativas excedido. Solicite um novo código.");
            }
            codeRepository.save(resetCode);
            throw new IllegalArgumentException("Código incorreto. Restam " + (3 - resetCode.getAttempts()) + " tentativa(s).");
        }

        // Sucesso: Redefinir a senha do usuário
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        // Inutilizar o código
        resetCode.setUsed(true);
        codeRepository.save(resetCode);
        log.info("Senha redefinida com sucesso para o usuário: {}", email);
    }

    private String generateSecureCode() {
        SecureRandom random = new SecureRandom();
        int code = 10000000 + random.nextInt(90000000); // 8 dígitos (10000000 a 99999999)
        return String.valueOf(code);
    }

    private String hashSha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash SHA-256", e);
        }
    }
}
