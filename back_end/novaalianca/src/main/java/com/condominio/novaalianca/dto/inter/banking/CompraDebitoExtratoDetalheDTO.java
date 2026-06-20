package com.condominio.novaalianca.dto.inter.banking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa os detalhes específicos de uma transação de Compra no Débito no Extrato Enriquecido.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompraDebitoExtratoDetalheDTO {

    private String estabelecimento;
    private String tipoDetalhe;
}
