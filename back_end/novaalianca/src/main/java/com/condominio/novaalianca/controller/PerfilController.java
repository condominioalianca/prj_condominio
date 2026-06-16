package com.condominio.novaalianca.controller;

import com.condominio.novaalianca.dto.PerfilDTO;
import com.condominio.novaalianca.services.PerfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/perfis")
public class PerfilController {

    @Autowired
    private PerfilService service;

    @GetMapping
    public ResponseEntity<List<PerfilDTO>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping("/save")
    public ResponseEntity<PerfilDTO> save(@Valid @RequestBody PerfilDTO dto) {
        return new ResponseEntity<>(service.save(dto), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<PerfilDTO> update(@Valid @RequestBody PerfilDTO dto) {
        return ResponseEntity.ok().body(service.update(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
