package com.condominio.novaalianca.controller;

import com.condominio.novaalianca.dto.boleto.BoletoDTO;
import com.condominio.novaalianca.dto.boleto.BoletoTESTEOLDDTO;
import com.condominio.novaalianca.services.boleto.BoletoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/boleto")
public class BoletoController {

    @Autowired
    private BoletoService boletoService;

    @GetMapping
    public ResponseEntity<Page<BoletoDTO>> findAll(Pageable pageable){
        Page<BoletoDTO> list = boletoService.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<Page<BoletoDTO>> findAllbyId(Pageable pageable, @PathVariable Long idUsuario){
        Page<BoletoDTO> list = boletoService.findAllPagedByIdUsuario(pageable,idUsuario);
        return ResponseEntity.ok().body(list);
    }

   // @PostMapping("/emitir/{id}")
    
}
