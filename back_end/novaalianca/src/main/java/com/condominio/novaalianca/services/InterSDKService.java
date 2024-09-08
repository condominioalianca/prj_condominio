package com.condominio.novaalianca.services;

import com.condominio.novaalianca.config.NovaAliancaProperties;
import com.condominio.novaalianca.util.CaminhoArquivosUtil;
import inter.InterSdk;
import inter.banking.model.ExtratoEnriquecido;
import inter.banking.model.FiltroConsultarExtratoEnriquecido;
import inter.banking.model.TransacaoEnriquecida;
import inter.cobranca.model.Boleto;
import inter.exceptions.SdkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InterSDKService {
    private static final Logger log = LoggerFactory.getLogger(InterSDKService.class);


    @Autowired
    private NovaAliancaProperties properties;

    @Autowired
    private CaminhoArquivosUtil caminhoArquivosUtil;

    public void emitirBoleto(Boleto boleto) throws SdkException {
        this.interSdk().cobranca().emitirBoleto(boleto);
    }

    public List<TransacaoEnriquecida> banking(String dataInicial , String dataFinal, FiltroConsultarExtratoEnriquecido filtroConsultarExtratoEnriquecido) throws SdkException {
        return this.interSdk().banking().consultarExtratoEnriquecido(dataInicial,dataFinal, filtroConsultarExtratoEnriquecido);
    }


    private InterSdk interSdk() throws SdkException {
        InterSdk interSdk = new InterSdk(properties.getClientId(), properties.getClientSecret(), caminhoArquivosUtil.caminhoCertificado(), properties.getSenhaCertificado());
        return interSdk;
    }
}
