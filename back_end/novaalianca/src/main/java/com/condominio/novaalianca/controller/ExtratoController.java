package com.condominio.novaalianca.controller;

import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.banking.models.entities.Extrato;
import com.condominio.novaalianca.banking.services.ExtratoService;
import com.condominio.novaalianca.dto.inter.banking.ExtratoEnriquecidoResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/extratos")
@RequiredArgsConstructor
public class ExtratoController {

    private final ExtratoService service;

    @GetMapping
    public ResponseEntity<List<Extrato>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Extrato> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping("/save")
    public ResponseEntity<Extrato> save(@RequestBody Extrato entity) {
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Extrato> update(@RequestBody Extrato entity) {
        return ResponseEntity.ok().body(service.update(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/enriquecer")
    public ResponseEntity<?> enriquecer(
            @org.springframework.web.bind.annotation.RequestParam("dataInicio") String dataInicio,
            @org.springframework.web.bind.annotation.RequestParam("dataFim") String dataFim) {
        
        try {
            java.time.format.DateTimeFormatter inputFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            java.time.format.DateTimeFormatter outputFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            java.time.LocalDate inicio = java.time.LocalDate.parse(dataInicio, inputFormatter);
            java.time.LocalDate fim = java.time.LocalDate.parse(dataFim, inputFormatter);
            
            String formattedInicio = inicio.format(outputFormatter);
            String formattedFim = fim.format(outputFormatter);
            
            ExtratoEnriquecidoResponseDTO result = 
                    service.getExtratoEnriquecido(formattedInicio, formattedFim, "PROD");
                    
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao enriquecer extrato: " + e.getMessage());
        }
    }
}