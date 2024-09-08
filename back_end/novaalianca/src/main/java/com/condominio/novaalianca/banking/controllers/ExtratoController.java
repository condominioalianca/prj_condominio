package com.condominio.novaalianca.banking.controllers;

import com.condominio.novaalianca.banking.services.ExtratoService;
import com.condominio.novaalianca.dto.boleto.BoletoDTO;
import inter.banking.model.FiltroConsultarExtratoEnriquecido;
import inter.banking.model.TransacaoEnriquecida;
import inter.exceptions.SdkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/extrato")
public class ExtratoController {

    @Autowired
    private ExtratoService extratoService;

    @GetMapping("/{dataInicio}/{dataFim}")
    public ResponseEntity<List<TransacaoEnriquecida>> findAll(
             @RequestBody (required = false) FiltroConsultarExtratoEnriquecido filtroConsultarExtratoEnriquecido,
            @PathVariable String dataInicio, @PathVariable String dataFim) throws SdkException {
        List<TransacaoEnriquecida> transacaoEnriquecidas = extratoService.getExtratoEnriquecido(dataInicio,dataFim,filtroConsultarExtratoEnriquecido);
        return ResponseEntity.ok().body(transacaoEnriquecidas);
    }

}
