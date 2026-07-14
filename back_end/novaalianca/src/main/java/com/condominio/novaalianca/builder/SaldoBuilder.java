package com.condominio.novaalianca.builder;

import com.condominio.novaalianca.banking.models.dtos.SaldoDTO;
import com.condominio.novaalianca.banking.models.entities.Saldo;
import com.condominio.novaalianca.dto.inter.banking.SaldoResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class SaldoBuilder {

    public Saldo toEntity(SaldoResponseDTO apiResponse) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (apiResponse == null) {
            return null;
        }
        return Saldo.builder()
                .bloqueadoCheque(apiResponse.getBloqueadoCheque())
                .disponivel(apiResponse.getDisponivel())
                .bloqueadoJudicialmente(apiResponse.getBloqueadoJudicialmente())
                .bloqueadoAdministrativo(apiResponse.getBloqueadoAdministrativo())
                .limite(apiResponse.getLimite())
                .dataReferencia(apiResponse.getDataReferencia() == null ? LocalDate.now().format(formatter) : apiResponse.getDataReferencia())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public SaldoDTO entityToDto(Saldo entity) {
        if (entity == null) {
            return null;
        }
        return SaldoDTO.builder()
                .id(entity.getId())
                .bloqueadoCheque(entity.getBloqueadoCheque())
                .disponivel(entity.getDisponivel())
                .bloqueadoJudicialmente(entity.getBloqueadoJudicialmente())
                .bloqueadoAdministrativo(entity.getBloqueadoAdministrativo())
                .limite(entity.getLimite())
                .dataReferencia(entity.getDataReferencia())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
