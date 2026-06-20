package com.condominio.novaalianca.dto.inter.banking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaldoResponseDTO {
    private Double bloqueadoCheque;
    private Double disponivel;
    private Double bloqueadoJudicialmente;
    private Double bloqueadoAdministrativo;
    private Double limite;
    private String dataReferencia;
}
