package com.condominio.novaalianca.controller;

import com.condominio.novaalianca.banking.models.dtos.ConciliacaoResponseDTO;
import com.condominio.novaalianca.banking.models.dtos.ExtratoConciliacaoPatchDTO;
import com.condominio.novaalianca.banking.models.dtos.ExtratoResumoDTO;
import com.condominio.novaalianca.banking.models.entities.Extrato;
import com.condominio.novaalianca.banking.models.enums.StatusConciliacao;
import com.condominio.novaalianca.banking.services.ConciliacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/conciliacao")
@RequiredArgsConstructor
public class ConciliacaoController {

    private final ConciliacaoService conciliacaoService;

    @GetMapping
    public ResponseEntity<List<ConciliacaoResponseDTO>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) StatusConciliacao status) {
        return ResponseEntity.ok(conciliacaoService.listar(dataInicio, dataFim, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Page<ExtratoResumoDTO>> listarExtratos(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(conciliacaoService.listarExtratosPaginado(id, pageable));
    }

    @PatchMapping("/extrato/{id}")
    public ResponseEntity<Extrato> atualizarExtrato(
            @PathVariable Long id,
            @RequestBody ExtratoConciliacaoPatchDTO dto) {
        return ResponseEntity.ok(conciliacaoService.atualizarExtrato(id, dto));
    }

    @DeleteMapping("/extrato/{id}")
    public ResponseEntity<Void> inativarExtrato(@PathVariable Long id) {
        conciliacaoService.inativarExtrato(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/gerar-pdf")
    public ResponseEntity<Void> gerarPdf(@PathVariable Long id) {
        conciliacaoService.gerarESalvarPdfConciliacao(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getPdf(@PathVariable Long id) {
        byte[] pdf = conciliacaoService.getPdfConciliacao(id);
        if (pdf == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "conciliacao_" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
