package com.condominio.novaalianca.controller;

import com.condominio.novaalianca.SchedulesTask.Shedules;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reprocessamento")
@RequiredArgsConstructor
public class ReprocessamentoController {

    private static final Logger log = LoggerFactory.getLogger(ReprocessamentoController.class);

    private final Shedules shedules;

    @PostMapping("/gerar-boleto")
    public ResponseEntity<String> forcarGeracaoBoleto() {
        try {
            log.info("Executando disparo manual do processo: Gerar Boleto (validaEnviodDeBoletos)");
            shedules.validaEnviodDeBoletos();
            return ResponseEntity.ok("Processo de geração de boletos executado com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao forçar geração de boleto: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erro ao executar geração de boleto: " + e.getMessage());
        }
    }

    @PostMapping("/recupera-boleto-detalhado")
    public ResponseEntity<String> forcarRecuperaBoletoDetalhado() {
        try {
            log.info("Executando disparo manual do processo: Recupera Boleto Detalhado (recuperaBoletoDetalhado)");
            shedules.recuperaBoletoDetalhado();
            return ResponseEntity.ok("Processo de recuperação de boletos detalhados executado com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao forçar recuperação de boleto detalhado: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erro ao executar recuperação detalhada: " + e.getMessage());
        }
    }

    @PostMapping("/envia-boleto")
    public ResponseEntity<String> forcarEnviaBoleto() {
        try {
            log.info("Executando disparo manual do processo: Envia Boleto (enviaEmail)");
            shedules.enviaEmail();
            return ResponseEntity.ok("Processo de envio de e-mails de boletos executado com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao forçar envio de e-mails de boletos: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erro ao executar envio de e-mails: " + e.getMessage());
        }
    }
}
