package com.condominio.novaalianca.dto.inter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que representa a resposta paginada do Extrato Enriquecido do Banco Inter.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtratoEnriquecidoResponseDTO {

    private Integer totalPaginas;
    private Integer totalElementos;
    private Boolean ultimaPagina;
    private Boolean primeiraPagina;
    private Integer tamanhoPagina;
    private Integer numeroDeElementos;
    private List<ExtratoEnriquecidoTransacaoDTO> transacoes;
}
