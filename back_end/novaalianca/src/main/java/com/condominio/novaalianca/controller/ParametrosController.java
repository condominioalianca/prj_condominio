package com.condominio.novaalianca.controller;

import com.condominio.novaalianca.dto.parametros.ParametrosDTO;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.services.ParametrosServices;
import com.inter.boletos.client.dto.boleto.BoletoDTO;
import com.inter.boletos.client.dto.boleto.ResponseBoletoDTO;
import com.inter.boletos.client.service.BoletoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
}
