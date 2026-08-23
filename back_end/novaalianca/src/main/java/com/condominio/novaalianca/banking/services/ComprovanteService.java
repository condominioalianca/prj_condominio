package com.condominio.novaalianca.banking.services;

import com.condominio.novaalianca.banking.models.entities.Comprovante;
import com.condominio.novaalianca.banking.models.entities.Extrato;
import com.condominio.novaalianca.banking.repositories.ComprovanteRepository;
import com.condominio.novaalianca.banking.repositories.ExtratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComprovanteService {

    private final ComprovanteRepository comprovanteRepository;
    private final ExtratoRepository extratoRepository;
    private final ConciliacaoService conciliacaoService;

    @Value("${app.upload-dir}")
    private String uploadDir;

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
        if (file.isEmpty()) {
            throw new RuntimeException("Arquivo vazio");
        }

        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String nomeSalvo = UUID.randomUUID().toString() + extension;

        Path fileTarget = path.resolve(nomeSalvo);
        Files.copy(file.getInputStream(), fileTarget);

        Comprovante comprovante = Comprovante.builder()
                .nomeArquivo(originalName)
                .tipoArquivo(file.getContentType())
                .nomeSalvo(nomeSalvo)
                .dados(null)
                .build();
        return comprovanteRepository.save(comprovante);
    }

    @Transactional(readOnly = true)
    public Comprovante downloadComprovante(Long idComprovante) {
        Comprovante comprovante = comprovanteRepository.findById(idComprovante)
                .orElseThrow(() -> new RuntimeException("Comprovante não encontrado"));

        if (comprovante.getDados() == null && comprovante.getNomeSalvo() != null) {
            try {
                Path filePath = Paths.get(uploadDir).resolve(comprovante.getNomeSalvo());
                if (Files.exists(filePath)) {
                    comprovante.setDados(Files.readAllBytes(filePath));
                } else {
                    throw new RuntimeException("Arquivo físico não encontrado no servidor.");
                }
            } catch (IOException e) {
                throw new RuntimeException("Erro ao ler arquivo físico: " + e.getMessage(), e);
            }
        }
        return comprovante;
    }
}
