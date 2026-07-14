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
public class ExtratoConciliacaoPatchDTO {
    private String descricao;
    private String comprovanteBase64;
    private Long idCategoriaGasto;
    private StatusConciliacao statusConciliado;
}
