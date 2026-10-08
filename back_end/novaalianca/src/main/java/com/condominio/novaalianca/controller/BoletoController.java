package com.condominio.novaalianca.controller;

import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.entities.BoletoNovaAlianca;
import com.condominio.novaalianca.cobranca.services.BoletoService;
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

import java.util.List;

@RestController
@RequestMapping(value = "/boletos")
@RequiredArgsConstructor
public class BoletoController {

    private final BoletoService service;
    private final com.condominio.novaalianca.cobranca.services.BoletoPdfComposerService pdfComposerService;
    private final com.condominio.novaalianca.services.UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<BoletoNovaAlianca>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoletoNovaAlianca> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @GetMapping("/exemplo-pdf/{usuarioId}")
    public ResponseEntity<byte[]> gerarPdfExemplo(@PathVariable Long usuarioId) throws Exception {
        com.condominio.novaalianca.entities.Usuario usuario = usuarioService.findByIDEntity(usuarioId);
        java.time.LocalDate hoje = java.time.LocalDate.now();

        // Gera folha de descritivo
        byte[] descritivoPdf = pdfComposerService.gerarDescritivoTaxasPdf(usuario, hoje);

        // Tenta buscar a conciliação do mês anterior se houver (mas sem bloquear visualização de exemplo se não houver)
        byte[] pdfFinal = descritivoPdf;
        try {
            pdfFinal = pdfComposerService.comporPdfCompleto(usuario, null, null, hoje);
        } catch (Exception e) {
            // Caso ocorra o bloqueio no teste de exemplo, geramos apenas o descritivo para visualização
            pdfFinal = descritivoPdf;
        }

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=boleto_exemplo_unidade_" + (usuario.getUnidade() != null ? usuario.getUnidade().getNumeroUnidade() : "0") + ".pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfFinal);
    }

    @GetMapping("/{id}/pdf-completo")
    public ResponseEntity<byte[]> baixarPdfCompleto(@PathVariable Long id) {
        BoletoNovaAlianca boleto = service.findById(id);
        com.condominio.novaalianca.entities.Usuario usuario = boleto.getUsuario();
        java.time.LocalDate dataRef = boleto.getDtEmissao() != null ? boleto.getDtEmissao() : java.time.LocalDate.now();

        byte[] pdfBoletoInterBytes = boleto.getArquivopdf();
        if (pdfBoletoInterBytes == null || pdfBoletoInterBytes.length == 0) {
            try {
                if (boleto.getCodSolicitacao() != null) {
                    pdfBoletoInterBytes = java.util.Base64.getDecoder().decode(service.downloadPDF(boleto.getCodSolicitacao(), "PRODUCAO"));
                }
            } catch (Exception e) {
                // Ignore download failure
            }
        }

        byte[] pdfFinal;
        try {
            pdfFinal = pdfComposerService.comporPdfCompleto(usuario, boleto, pdfBoletoInterBytes, dataRef);
        } catch (Exception e) {
            // Se a conciliação do mês anterior não estiver BATIDA, gera o Boleto Inter + Descritivo de Taxas
            byte[] descritivoBytes = pdfComposerService.gerarDescritivoTaxasPdf(usuario, dataRef);
            java.util.List<byte[]> listaPdfs = new java.util.ArrayList<>();
            if (pdfBoletoInterBytes != null && pdfBoletoInterBytes.length > 0) {
                listaPdfs.add(pdfBoletoInterBytes);
            }
            if (descritivoBytes != null && descritivoBytes.length > 0) {
                listaPdfs.add(descritivoBytes);
            }
            pdfFinal = pdfComposerService.juntarPdfs(listaPdfs);
        }

        String fileName = "Boleto_Completo_" + (boleto.getNossoNumero() != null ? boleto.getNossoNumero() : id) + ".pdf";

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName)
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfFinal);
    }

    @PostMapping("/save")
    public ResponseEntity<BoletoNovaAlianca> save(@RequestBody BoletoNovaAlianca entity) {
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<BoletoNovaAlianca> update(@RequestBody BoletoNovaAlianca entity) {
        return ResponseEntity.ok().body(service.update(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}