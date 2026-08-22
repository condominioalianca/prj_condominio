package com.condominio.novaalianca.builder;

import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.config.NovaAliancaProperties;
import com.condominio.novaalianca.dto.boleto.RequestBoleto;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestBoletoBuilder {

    private final NovaAliancaProperties properties;
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