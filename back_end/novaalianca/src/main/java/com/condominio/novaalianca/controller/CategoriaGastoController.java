package com.condominio.novaalianca.controller;

import com.condominio.novaalianca.banking.models.entities.CategoriaGasto;
import com.condominio.novaalianca.banking.services.CategoriaGastoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categoria-gasto")
@RequiredArgsConstructor
public class CategoriaGastoController {

    private final CategoriaGastoService categoriaGastoService;

    @GetMapping
    public ResponseEntity<List<CategoriaGasto>> listarAtivas() {
        return ResponseEntity.ok(categoriaGastoService.listarAtivas());
    }

    @PostMapping
    public ResponseEntity<CategoriaGasto> criar(@RequestBody CategoriaGasto categoriaGasto) {
        return ResponseEntity.ok(categoriaGastoService.salvar(categoriaGasto));
    }
}
