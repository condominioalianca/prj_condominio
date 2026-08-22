package com.condominio.novaalianca.controller;

import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.dto.EmpresaDTO;
import com.condominio.novaalianca.services.EmpresaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService service;

    @GetMapping
    public ResponseEntity<List<EmpresaDTO>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<EmpresaDTO> findByNrDocumento(@RequestParam String documento) {
        return ResponseEntity.ok().body(service.findByNrDocumento(documento));
    }

    @PostMapping("/save")
    public ResponseEntity<EmpresaDTO> save(@Valid @RequestBody EmpresaDTO dto) {
        return new ResponseEntity<>(service.save(dto), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<EmpresaDTO> update(@Valid @RequestBody EmpresaDTO dto) {
        return ResponseEntity.ok().body(service.update(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}