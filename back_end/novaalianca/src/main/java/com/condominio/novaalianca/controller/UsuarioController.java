package com.condominio.novaalianca.controller;

import com.condominio.novaalianca.dto.UsuarioDTO;
import com.condominio.novaalianca.services.UsuarioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    public UsuarioDTO usuarioSave(@RequestBody UsuarioDTO usuarioDTO){
        return service.usuarioSave(usuarioDTO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findByID(id));

    }

    @DeleteMapping("/delet/{idUsuario}")
    @Transactional
    public ResponseEntity<Void> usuarioDelete(@PathVariable Long idUsuario){
        service.deletById(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
