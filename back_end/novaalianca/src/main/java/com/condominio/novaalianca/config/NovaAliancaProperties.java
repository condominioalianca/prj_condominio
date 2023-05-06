package com.condominio.novaalianca.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Getter
@Configuration
public class NovaAliancaProperties {

    @Value("${banco.inter.url.token}")
    private String bancoInterUrlToken;

    @Value("${banco.inter.url.boleto}")
    private String bancoInterUrlBoleto;

    @Value("${banco.inter.caminho.certificado}")
    private String caminhoCertificado;

    @Value("${banco.inter.senha.certificado}")
    private String senhaCertificado;

    @Value("${banco.inter.client.id}")
    private String clientId;

    @Value("${banco.inter.client.secret}")
    private String clientSecret;

    @Value("${banco.inter.grant.type}")
    private String grantType;


    @Value("${boleto.cpfcnpj.benificiario}")
    private String cnpjCpfBenificiario;

    //Propriedades dos Emails
    @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.port}")
    private String port;

    @Value("${mail.smtp.username}")
    private String usernameMail;

    @Value("${mail.smtp.password}")
    private String senhaMail;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${security.oauth2.client.client-id}")
    private String clientIdOauth2;

    @Value("${security.oauth2.client.client-secret}")
    private  String clientSecretOauth2;

    @Value("${jwt.duration}")
    private Integer jwtDuration;


}
