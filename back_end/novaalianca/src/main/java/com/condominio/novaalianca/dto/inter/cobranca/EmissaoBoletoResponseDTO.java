package com.condominio.novaalianca.dto.inter.cobranca;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa a resposta do Banco Inter após a solicitação de emissão de um boleto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmissaoBoletoResponseDTO {

    private String codigoSolicitacao;
}
