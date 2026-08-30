package com.condominio.novaalianca.controller;

import com.condominio.novaalianca.dto.PasswordResetConfirmDTO;
import com.condominio.novaalianca.dto.PasswordResetRequestDTO;
import com.condominio.novaalianca.services.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/auth/password-reset")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/request")
    public ResponseEntity<?> requestReset(@Valid @RequestBody PasswordResetRequestDTO requestDTO) {
        passwordResetService.requestPasswordReset(requestDTO.getEmail());
        return ResponseEntity.ok(Map.of("message", "Código de recuperação enviado com sucesso para o e-mail cadastrado."));
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmReset(@Valid @RequestBody PasswordResetConfirmDTO confirmDTO) {
        passwordResetService.confirmPasswordReset(
                confirmDTO.getEmail(),
                confirmDTO.getCode(),
                confirmDTO.getNewPassword()
        );
        return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso."));
    }
}
