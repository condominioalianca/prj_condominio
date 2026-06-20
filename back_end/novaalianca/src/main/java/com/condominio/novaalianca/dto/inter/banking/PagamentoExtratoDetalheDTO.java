package com.condominio.novaalianca.dto.inter.banking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa os detalhes específicos de uma transação de Pagamento no Extrato Enriquecido.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagamentoExtratoDetalheDTO {

    private String valorTotal;
    private String detalheDescricao;
    private String contaBancaria;
    private String agencia;
    private String adicionado;
    private String dataVencimento;
    private String codigoAfiliado;
    private String empresaEmissora;
    private String valorOriginal;
    private String desconto;
    private String cpfCnpj;
    private String valorPrincipal;
    private String periodoApuracao;
    private String valorAumentado;
    private String codBarras;
    private String valorParcial;
    private String hora;
    private String juros;
    private String multa;
    private String empresaOrigem;
    private String nomeDestinatario;
    private String tipoDetalhe;
    private String nomeOrigem;
    private String codigoReceita;
    private String linhaDigitavel;
    private String autenticacao;
}
