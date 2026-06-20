package com.condominio.novaalianca.controller;

import com.condominio.novaalianca.dto.parametros.ParametrosDTO;
import com.condominio.novaalianca.services.ParametrosServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@RequestMapping(value = "/parametros")
public class ParametrosController {

    @Autowired
    private ParametrosServices services;

    @GetMapping("/")
    public ResponseEntity<?> findAll() throws Exception {

        return ResponseEntity.ok().body( services.listParametros());
    }

    @GetMapping("/{idSistema}")
    public ResponseEntity<?> findById(@PathVariable Long idSistema) throws Exception {

        return ResponseEntity.ok().body( services.findById(idSistema));
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody ParametrosDTO parametrosDTO){
        return  new ResponseEntity<>(services.save(parametrosDTO), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<ParametrosDTO> update(@RequestBody ParametrosDTO dto) {
        return ResponseEntity.ok().body(services.update(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        services.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
