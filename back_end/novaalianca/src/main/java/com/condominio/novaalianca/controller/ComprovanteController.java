package com.condominio.novaalianca.controller;

import com.condominio.novaalianca.banking.models.entities.Comprovante;
import com.condominio.novaalianca.banking.services.ComprovanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/comprovante")
@RequiredArgsConstructor
public class ComprovanteController {

    private final ComprovanteService comprovanteService;

    @PostMapping("/extrato/{idExtrato}")
    public ResponseEntity<Void> uploadIndividual(
            @PathVariable Long idExtrato, 
            @RequestParam("file") MultipartFile file) throws IOException {
        comprovanteService.uploadComprovanteIndividual(idExtrato, file);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/conciliacao/{idConciliacao}")
    public ResponseEntity<Void> uploadEmLote(
            @PathVariable Long idConciliacao, 
            @RequestParam("file") MultipartFile file) throws IOException {
        comprovanteService.uploadComprovanteEmLote(idConciliacao, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{idComprovante}")
    public ResponseEntity<byte[]> downloadComprovante(@PathVariable Long idComprovante) {
        Comprovante comprovante = comprovanteService.downloadComprovante(idComprovante);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + comprovante.getNomeArquivo() + "\"")
                .contentType(MediaType.parseMediaType(comprovante.getTipoArquivo() != null ? comprovante.getTipoArquivo() : "application/octet-stream"))
                .body(comprovante.getDados());
    }
}
