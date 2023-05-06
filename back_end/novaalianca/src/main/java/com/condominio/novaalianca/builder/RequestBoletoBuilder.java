package com.condominio.novaalianca.builder;

import com.condominio.novaalianca.config.NovaAliancaProperties;
import com.condominio.novaalianca.dto.boleto.RequestBoleto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestBoletoBuilder {

    @Autowired
    private NovaAliancaProperties properties;
    public RequestBoleto requestBoleto (String scope){
        return RequestBoleto.builder()
                .grantType(properties.getGrantType())
                .caminhoCertificado(properties.getCaminhoCertificado())
                .senhaCertificado(properties.getSenhaCertificado())
                .scope(scope)
                .clientId(properties.getClientId())
                .clientIdSecret(properties.getClientSecret())
                .urlBancoInterToken(properties.getBancoInterUrlToken())
                .urlBancoInterBoleto(properties.getBancoInterUrlBoleto())
                .build();
    }

}
