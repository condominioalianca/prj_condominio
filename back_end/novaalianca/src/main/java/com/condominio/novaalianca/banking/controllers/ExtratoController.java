package com.condominio.novaalianca.banking.controllers;

import com.condominio.novaalianca.banking.models.entities.Extrato;
import com.condominio.novaalianca.banking.services.ExtratoService;
import inter.banking.model.FiltroConsultarExtratoEnriquecido;
import inter.exceptions.SdkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/extrato")
public class ExtratoController {

    @Autowired
    private ExtratoService extratoService;

    @PostMapping("/{dataInicio}/{dataFim}")
    public ResponseEntity<List<Extrato>> findAll(
             @RequestBody (required = false) FiltroConsultarExtratoEnriquecido filtroConsultarExtratoEnriquecido,
            @PathVariable String dataInicio, @PathVariable String dataFim) throws SdkException {
        List<Extrato> listExtrato = extratoService.getExtratoEnriquecido(dataInicio,dataFim);
        return ResponseEntity.ok().body(listExtrato);
    }

}
