package com.condominio.novaalianca.dto.boleto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class RequestBoleto {

    String caminhoCertificado;
    String senhaCertificado;
    String urlBancoInterToken;
    String urlBancoInterBoleto;
    String clientId;
    String clientIdSecret;
    String grantType;
    String scope;
}
