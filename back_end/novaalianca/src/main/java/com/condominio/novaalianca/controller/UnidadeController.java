package com.condominio.novaalianca.controller;


import com.condominio.novaalianca.dto.UnidadeDTO;
import com.condominio.novaalianca.services.UnidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/unidade")
public class UnidadeController {

    @Autowired
    private UnidadeService service;

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

    @DeleteMapping("/delet/{idUnidade}")
    public ResponseEntity<Void> enderecoDelet(@PathVariable Long idUnidade){
        service.deletById(idUnidade);
        return ResponseEntity.noContent().build();
    }
}
