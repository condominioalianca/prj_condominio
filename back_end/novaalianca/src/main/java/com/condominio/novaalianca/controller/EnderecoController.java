package com.condominio.novaalianca.controller;

import com.condominio.novaalianca.dto.EnderecoDTO;
import com.condominio.novaalianca.services.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;


@RestController
@RequestMapping(value = "/endereco")
public class EnderecoController {

    @Autowired
    private EnderecoService service;

    @GetMapping
    public ResponseEntity<Page<EnderecoDTO>> findAll(Pageable pageable) {
        Page<EnderecoDTO> list = service.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/save")
    public EnderecoDTO enderecoSave(@RequestBody EnderecoDTO enderecoDTO) {

        return service.enderecoSave(enderecoDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findByID(id));

    }

    @DeleteMapping("/delet/{idEndereco}")
    @Transactional
    public ResponseEntity<Void> enderecoDelete(@PathVariable Long idEndereco) {
        service.deletById(idEndereco);
        return ResponseEntity.noContent().build();
    }
}
