package com.condominio.novaalianca.controller;

import com.condominio.novaalianca.banking.models.dtos.SaldoDTO;
import com.condominio.novaalianca.banking.services.SaldoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/saldo")
public class SaldoController {

    @Autowired
    private SaldoService service;

    @GetMapping("/atual")
    public ResponseEntity<SaldoDTO> getSaldoAtual() {
        SaldoDTO saldo = service.obterSaldoMaisAtual();
        if (saldo == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(saldo);
    }
}
