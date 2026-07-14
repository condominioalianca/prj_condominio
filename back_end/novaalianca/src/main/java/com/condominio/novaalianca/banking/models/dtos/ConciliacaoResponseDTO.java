package com.condominio.novaalianca.banking.models.dtos;

import com.condominio.novaalianca.banking.models.enums.StatusConciliacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConciliacaoResponseDTO {
    private Long id;
    private String descricao;
    private StatusConciliacao status;
    private Long qtdBatido;
    private Long qtdDivergente;
    private Long qtdTotal;

    public ConciliacaoResponseDTO(Long id, String descricao, Long qtdBatido, Long qtdDivergente) {
        this.id = id;
        this.descricao = descricao;
        this.qtdBatido = qtdBatido != null ? qtdBatido : 0L;
        this.qtdDivergente = qtdDivergente != null ? qtdDivergente : 0L;
        this.qtdTotal = this.qtdBatido + this.qtdDivergente;

        if (this.qtdDivergente > 0) {
            this.status = StatusConciliacao.PENDENTE;
        } else {
            this.status = StatusConciliacao.BATIDO;
        }
    }
}
