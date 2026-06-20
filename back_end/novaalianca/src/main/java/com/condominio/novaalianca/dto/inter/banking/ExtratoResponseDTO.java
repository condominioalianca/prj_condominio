package com.condominio.novaalianca.dto.inter.banking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que encapsula o retorno da lista de transações da API do Banco Inter.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtratoResponseDTO {

    private List<TransacaoSimplesDTO> transacoes;
}
