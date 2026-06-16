package com.condominio.novaalianca.dto.inter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa os detalhes específicos de uma transação de Boleto de Cobrança no Extrato Enriquecido.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BoletoCobrancaExtratoDetalheDTO {

    private String dataVencimento;
    private String dataTransacao;
    private String nossoNumero;
    private String seuNumero;
    private String codBarras;
    private String juros;
    private String multa;
    private String desconto1;
    private String desconto2;
    private String desconto3;
    private String nome;
    private String dataLimite;
    private String tipoDetalhe;
    private String cpfCnpj;
    private String dataEmissao;
    private String abatimento;
}
