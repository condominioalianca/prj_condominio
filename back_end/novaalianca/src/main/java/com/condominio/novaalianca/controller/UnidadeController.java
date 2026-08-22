package com.condominio.novaalianca.controller;


import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.dto.UnidadeDTO;
import com.condominio.novaalianca.services.UnidadeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/unidade")
@RequiredArgsConstructor
public class UnidadeController {

    private final UnidadeService service;

    @GetMapping
    public ResponseEntity<Page<UnidadeDTO>>findAll(Pageable pageable){
        Page<UnidadeDTO> list = service.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/save")
    public UnidadeDTO unidadeSave(@RequestBody UnidadeDTO unidadeDTO){

        return service.unidadeSave(unidadeDTO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findByID(id));

    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUnidade(@RequestBody UnidadeDTO unidadeDTO) {
        service.unidadeUpdate(unidadeDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> unidadeDelete(@PathVariable Long id){
        service.deletById(id);
        return ResponseEntity.noContent().build();
    }
}