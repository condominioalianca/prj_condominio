package com.condominio.novaalianca.banking.services;

import com.condominio.novaalianca.banking.models.entities.Extrato;
import com.condominio.novaalianca.config.NovaAliancaProperties;
import com.condominio.novaalianca.services.InterSDKService;
import com.condominio.novaalianca.util.CaminhoArquivosUtil;
import inter.banking.model.FiltroConsultarExtratoEnriquecido;
import inter.banking.model.TransacaoEnriquecida;
import inter.exceptions.SdkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ExtratoService {

    @Autowired
    InterSDKService interSDKService;

    public List<TransacaoEnriquecida> getExtratoEnriquecido(String dataInicio, String dataFim, FiltroConsultarExtratoEnriquecido filtro) throws SdkException {
        DateTimeFormatter formatterLocalDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<TransacaoEnriquecida> listTransacoes = interSDKService.banking(dataInicio, dataFim, filtro.builder().build());
        if(!listTransacoes.isEmpty()){
            List<Extrato> listExtrato = new ArrayList<>();
            listTransacoes.forEach(listTransacao -> {
                Extrato extrato = new Extrato();
                extrato.setIdTransacao(listTransacao.getIdTransacao());
                extrato.setDtInclusao(LocalDate.parse(listTransacao.getDataTransacao(),formatterLocalDate));
                extrato.setTipoTransacao(listTransacao.getTipoTransacao());
                extrato.setTipoOperacao(listTransacao.getTipoOperacao());
                extrato.setTituloTransacao(listTransacao.getTitulo());
                extrato.setValorTransacao(Double.valueOf(listTransacao.getValor()));
                if(Objects.nonNull(listTransacao.getDetalhes())){
//                    extrato.setNomeRecebedor(listTransacao.getDetalhes().);
//                    extrato.setDocumenteRecebedor();
//                    extrato.setNomePagador();
//                    extrato.setDocumentePagador();
                }

                listExtrato.add(extrato);
            });
        }



        return listTransacoes;
    }
}
