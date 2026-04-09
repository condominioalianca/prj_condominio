package com.condominio.novaalianca.cobranca.builder;

import com.condominio.novaalianca.cobranca.models.dto.CobrancaDTO;
import com.condominio.novaalianca.entities.BoletoNovaAlianca;
import inter.pix.model.Cobranca;
import org.springframework.stereotype.Component;

@Component
public class CobrancaBuilder {

    public static CobrancaDTO entityToCobrancaDTO(BoletoNovaAlianca boletoNovaAlianca) {
        return  CobrancaDTO.builder()
                .id(boletoNovaAlianca.getId())
                //TODO Criar Parametro no Banco
//                .codigoSolicitacao()
                .seuNumero(boletoNovaAlianca.getSeuNumero())
                .dataEmissao(boletoNovaAlianca.getDtEmissao().toString())
                .dataVencimento(boletoNovaAlianca.getDtVencimento().toString())
                .valorNominal(boletoNovaAlianca.getValor().toString())
                //TODO Criar Parametro no Banco
//                .tipoCobranca(boletoNovaAlianca.)
                .situacao(boletoNovaAlianca.getTxSituacao())
                .dataSituacao(boletoNovaAlianca.getDhSituacao().toString())
                .valorTotalRecebido(boletoNovaAlianca.getValorPagamento().toString())
                .origemRecebimento(boletoNovaAlianca.getTxOrigem())
                .motivoCancelamento(boletoNovaAlianca.getTxCancelamento())
                .arquivada(boletoNovaAlianca.getAtivo())
                .descontos(DescontosBuilder.listDescontos())
                .multa(MultaBuilder.multa())
                .mora(MoraBuilder.moraDTOBuilder())
                .pagador(PagadorBuilder.pagadorDTOBuilder(boletoNovaAlianca))
                .build();

    }
}
