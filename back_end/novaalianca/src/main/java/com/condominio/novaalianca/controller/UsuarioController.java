package com.condominio.novaalianca.controller;

import com.condominio.novaalianca.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.dto.UsuarioCadastroDTO;
import com.condominio.novaalianca.services.UsuarioService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;


@RestController
@RequestMapping(value = "/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    public ResponseEntity<Page<UsuarioDTO>>findAll(Pageable pageable){
        Page<UsuarioDTO> list = service.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findByID(id));
    }

    @GetMapping("/search")
    public ResponseEntity<UsuarioDTO> findByDocumentoOrEmail(@RequestParam String value) {
        return ResponseEntity.ok().body(service.findByDocumentoOrEmail(value));
    }

    @PostMapping("/save")
    public ResponseEntity<?> usuarioSave(@Valid @RequestBody UsuarioDTO usuarioInsertDTO){
        return ResponseEntity.ok().body(service.usuarioSave(usuarioInsertDTO));

    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarUsuario(@Valid @RequestBody UsuarioCadastroDTO usuarioCadastroDTO){
        return ResponseEntity.ok().body(service.cadastrarUsuario(usuarioCadastroDTO));
    }

    @PutMapping("/update")
    public ResponseEntity<Void> usuarioUpdate(@Valid @RequestBody UsuarioDTO usuarioInsertDTO){
        service.usuarioUpdate(usuarioInsertDTO);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/delet/{idUsuario}")
    public ResponseEntity<Void> usuarioDelete(@PathVariable Long idUsuario){
        service.deletById(idUsuario);
        return ResponseEntity.noContent().build();
    }
}