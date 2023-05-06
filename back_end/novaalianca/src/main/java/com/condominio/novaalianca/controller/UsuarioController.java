package com.condominio.novaalianca.controller;

import com.condominio.novaalianca.dto.UsuarioDTO;
import com.condominio.novaalianca.dto.UsuarioInsertDTO;
import com.condominio.novaalianca.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    public ResponseEntity<Page<UsuarioDTO>>findAll(Pageable pageable){
        Page<UsuarioDTO> list = service.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/save")
    public UsuarioDTO usuarioSave(@Valid @RequestBody UsuarioInsertDTO usuarioInsertDTO){
        return service.usuarioSave(usuarioInsertDTO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findByID(id));

    }

    @DeleteMapping("/delet/{idUsuario}")
    public ResponseEntity<Void> usuarioDelete(@PathVariable Long idUsuario){
        service.deletById(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
