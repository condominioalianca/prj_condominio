package com.condominio.novaalianca.dto.inter.banking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa os detalhes específicos de uma transação PIX no Extrato Enriquecido.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PixExtratoDetalheDTO {

    private String txId;
    private String nomePagador;
    private String descricaoPix;
    private String cpfCnpjPagador;
    private String contaBancariaRecebedor;
    private String nomeEmpresaPagador;
    private String tipoDetalhe;
    private String endToEndId;
    private String chavePixRecebedor;
    private String nomeEmpresaRecebedor;
    private String nomeRecebedor;
    private String agenciaRecebedor;
    private String cpfCnpjRecebedor;
    private String origemMovimentacao;
    private String codigoSolicitacao;
}
