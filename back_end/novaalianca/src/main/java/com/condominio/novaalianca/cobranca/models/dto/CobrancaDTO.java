package com.condominio.novaalianca.cobranca.models.dto;



import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class CobrancaDTO {
    private Long id;
    private String codigoSolicitacao;
    private String seuNumero;
    private String dataEmissao;
    private String dataVencimento;
    private String valorNominal;
    private String tipoCobranca;
    private String situacao;
    private String dataSituacao;
    private String valorTotalRecebido;
    private String origemRecebimento;
    private String motivoCancelamento;
    private Boolean arquivada;
    private List<DescontoDTO> descontos;
    private MultaDTO multa;
    private MoraDTO mora;
    private PagadorDTO pagador;


}
