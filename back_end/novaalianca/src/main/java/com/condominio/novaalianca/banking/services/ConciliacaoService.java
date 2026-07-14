package com.condominio.novaalianca.banking.services;

import com.condominio.novaalianca.banking.models.dtos.ConciliacaoResponseDTO;
import com.condominio.novaalianca.banking.models.dtos.ExtratoConciliacaoPatchDTO;
import com.condominio.novaalianca.banking.models.entities.Conciliacao;
import com.condominio.novaalianca.banking.models.entities.Extrato;
import com.condominio.novaalianca.banking.models.entities.CategoriaGasto;
import com.condominio.novaalianca.banking.models.dtos.ExtratoResumoDTO;
import com.condominio.novaalianca.banking.models.entities.Saldo;
import com.condominio.novaalianca.banking.models.enums.StatusConciliacao;
import com.condominio.novaalianca.banking.models.enums.StatusGeral;
import com.condominio.novaalianca.banking.repositories.ConciliacaoRepository;
import com.condominio.novaalianca.banking.repositories.ExtratoRepository;
import com.condominio.novaalianca.banking.repositories.CategoriaGastoRepository;
import com.condominio.novaalianca.banking.repositories.SaldoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ConciliacaoService {

    private final ConciliacaoRepository conciliacaoRepository;
    private final ExtratoRepository extratoRepository;
    private final CategoriaGastoRepository categoriaGastoRepository;
    private final RelatorioConciliacaoService relatorioConciliacaoService;
    private final SaldoRepository saldoRepository;

    @Transactional(readOnly = true)
    public List<ConciliacaoResponseDTO> listar(LocalDate dataInicio, LocalDate dataFim, StatusConciliacao status) {
        return conciliacaoRepository.findResumoWithFilters(
                dataInicio, dataFim, status,
                StatusConciliacao.BATIDO, StatusConciliacao.PENDENTE
        );
    }


    @Transactional(readOnly = true)
    public List<Extrato> listarExtratos(Long conciliacaoId) {
        Conciliacao c = conciliacaoRepository.findById(conciliacaoId)
                .orElseThrow(() -> new RuntimeException("Conciliação não encontrada"));
        return c.getExtratos().stream()
                .filter(e -> e.getStatusGeral() != StatusGeral.INATIVO)
                .collect(Collectors.toList());
    }

    public Page<ExtratoResumoDTO> listarExtratosPaginado(Long conciliacaoId, Pageable pageable) {
        return extratoRepository.findResumoByConciliacaoId(conciliacaoId, pageable);
    }

    @Transactional
    public Extrato atualizarExtrato(Long extratoId, ExtratoConciliacaoPatchDTO dto) {
        Extrato extrato = extratoRepository.findById(extratoId)
                .orElseThrow(() -> new RuntimeException("Extrato não encontrado"));

        if (dto.getDescricao() != null) {
            extrato.setDescricao(dto.getDescricao());
        }
        
        // A lógica de comprovante agora fica no ComprovanteService (Upload separado)
        
        if (dto.getIdCategoriaGasto() != null) {
            CategoriaGasto categoria = categoriaGastoRepository.findById(dto.getIdCategoriaGasto())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            extrato.setCategoriaGasto(categoria);
        }
        
        if (dto.getStatusConciliado() != null) {
            extrato.setStatusConciliado(dto.getStatusConciliado());
        }

        extratoRepository.save(extrato);

        verificarStatusConciliacao(extrato.getConciliacao());

        return extrato;
    }

    @Transactional
    public void inativarExtrato(Long extratoId) {
        Extrato extrato = extratoRepository.findById(extratoId)
                .orElseThrow(() -> new RuntimeException("Extrato não encontrado"));
        extrato.setStatusGeral(StatusGeral.INATIVO);
        extratoRepository.save(extrato);
        verificarStatusConciliacao(extrato.getConciliacao());
    }

    private void verificarStatusConciliacao(Conciliacao conciliacao) {
        if (conciliacao == null || conciliacao.getExtratos() == null || conciliacao.getExtratos().isEmpty()) return;
        
        boolean todosBatidos = conciliacao.getExtratos().stream()
                .filter(e -> e.getStatusGeral() != StatusGeral.INATIVO)
                .allMatch(e -> StatusConciliacao.BATIDO.equals(e.getStatusConciliado()));

        StatusConciliacao novoStatus = todosBatidos ? StatusConciliacao.BATIDO : StatusConciliacao.PENDENTE;
        
        if (!novoStatus.equals(conciliacao.getStatus())) {
            conciliacao.setStatus(novoStatus);
            conciliacao.setDataAtualizacao(LocalDateTime.now());
            conciliacaoRepository.save(conciliacao);
        }
    }

    @Transactional
    public Conciliacao findOrCreateByDataReferencia(LocalDate dataTransacao) {
        LocalDate dataReferencia = dataTransacao.withDayOfMonth(1);
        return conciliacaoRepository.findByDataReferencia(dataReferencia).orElseGet(() -> {
            Conciliacao nova = new Conciliacao();
            nova.setDataReferencia(dataReferencia);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
            nova.setDescricao("Conciliação - " + dataReferencia.format(formatter));
            nova.setStatus(StatusConciliacao.PENDENTE);
            nova.setDataCriacao(LocalDateTime.now());
            return conciliacaoRepository.save(nova);
        });
    }

    @Transactional
    public void gerarESalvarPdfConciliacao(Long conciliacaoId) {
        Conciliacao conciliacao = conciliacaoRepository.findById(conciliacaoId)
                .orElseThrow(() -> new RuntimeException("Conciliação não encontrada"));

        String mesReferencia = conciliacao.getDataReferencia().format(DateTimeFormatter.ofPattern("MM/yyyy"));
        Saldo saldo = saldoRepository.findFirstByDataReferenciaEndingWithOrderByIdDesc(mesReferencia).orElse(null);

        byte[] pdfBytes = relatorioConciliacaoService.gerarPdfConciliacao(conciliacao, saldo);
        conciliacao.setArquivoPdf(pdfBytes);
        conciliacaoRepository.save(conciliacao);
    }

    @Transactional(readOnly = true)
    public byte[] getPdfConciliacao(Long conciliacaoId) {
        Conciliacao conciliacao = conciliacaoRepository.findById(conciliacaoId)
                .orElseThrow(() -> new RuntimeException("Conciliação não encontrada"));
        return conciliacao.getArquivoPdf();
    }
}
