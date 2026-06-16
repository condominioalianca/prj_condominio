package com.condominio.novaalianca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CobrancaExtraDTO {
    private Long idCobrancaExtra;
    private Double valorCobranca;
    private LocalDate dtInclusao;
    private Long mesReferencia;
    private String descricao;
    private Long idUnidade;
}
