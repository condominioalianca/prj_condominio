package com.condominio.novaalianca.banking.services;

import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.banking.models.dtos.SaldoDTO;
import com.condominio.novaalianca.banking.models.entities.Saldo;
import com.condominio.novaalianca.banking.repositories.SaldoRepository;
import com.condominio.novaalianca.builder.SaldoBuilder;
import com.condominio.novaalianca.dto.inter.banking.SaldoResponseDTO;
import com.condominio.novaalianca.services.inter.InterService;
import com.condominio.novaalianca.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SaldoService {

    private static final Logger log = LoggerFactory.getLogger(SaldoService.class);

    private final InterService interService;

    private final SaldoRepository saldoRepository;

    private final SaldoBuilder saldoBuilder;

    private final DateUtils dateUtils;

    public SaldoDTO obterSaldoMaisAtual() {
        return saldoRepository.findFirstByOrderByIdDesc()
                .map(saldoBuilder::entityToDto)
                .orElse(null);
    }

    public SaldoDTO atualizarSaldo(String ambiente) {
        try {
            log.info("Iniciando busca do Saldo da Conta Corrente no ambiente: {}", ambiente);
            String dataSaldo = dateUtils.localDateToStringYYYYMMDD(LocalDate.now());
            SaldoResponseDTO responseDTO = interService.buscarSaldo(dataSaldo, null, ambiente);

            if (responseDTO != null) {
                // Usando o componente de builder para converter o DTO da API em Entidade
                Saldo saldo = saldoBuilder.toEntity(responseDTO);
                
                // Salvando a entidade no banco de dados
                Saldo salvo = saldoRepository.save(saldo);
                log.info("Saldo persistido com sucesso. ID: {}, Disponível: {}", salvo.getId(), salvo.getDisponivel());
                
                // Retornando o DTO do sistema
                return saldoBuilder.entityToDto(salvo);
            }
        } catch (Exception e) {
            log.error("Erro ao atualizar e persistir saldo do Banco Inter: {}", e.getMessage(), e);
        }
        return null;
    }
}