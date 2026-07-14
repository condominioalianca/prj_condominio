package com.condominio.novaalianca.banking.services;

import com.condominio.novaalianca.banking.models.entities.Comprovante;
import com.condominio.novaalianca.banking.models.entities.Extrato;
import com.condominio.novaalianca.banking.repositories.ComprovanteRepository;
import com.condominio.novaalianca.banking.repositories.ExtratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComprovanteService {

    private final ComprovanteRepository comprovanteRepository;
    private final ExtratoRepository extratoRepository;
    private final ConciliacaoService conciliacaoService;

    @Transactional
    public Comprovante uploadComprovanteIndividual(Long idExtrato, MultipartFile file) throws IOException {
        Extrato extrato = extratoRepository.findById(idExtrato)
                .orElseThrow(() -> new RuntimeException("Extrato não encontrado"));

        Comprovante comprovante = salvarArquivo(file);
        extrato.setComprovante(comprovante);
        extratoRepository.save(extrato);

        return comprovante;
    }

    @Transactional
    public Comprovante uploadComprovanteEmLote(Long idConciliacao, MultipartFile file) throws IOException {
        List<Extrato> extratos = conciliacaoService.listarExtratos(idConciliacao);
        
        if (extratos.isEmpty()) {
            throw new RuntimeException("Nenhum extrato ativo encontrado para esta conciliação.");
        }

        Comprovante comprovante = salvarArquivo(file);
        
        for (Extrato extrato : extratos) {
            extrato.setComprovante(comprovante);
        }
        extratoRepository.saveAll(extratos);

        return comprovante;
    }

    private Comprovante salvarArquivo(MultipartFile file) throws IOException {
        Comprovante comprovante = Comprovante.builder()
                .nomeArquivo(file.getOriginalFilename())
                .tipoArquivo(file.getContentType())
                .dados(file.getBytes())
                .build();
        return comprovanteRepository.save(comprovante);
    }

    @Transactional(readOnly = true)
    public Comprovante downloadComprovante(Long idComprovante) {
        return comprovanteRepository.findById(idComprovante)
                .orElseThrow(() -> new RuntimeException("Comprovante não encontrado"));
    }
}
