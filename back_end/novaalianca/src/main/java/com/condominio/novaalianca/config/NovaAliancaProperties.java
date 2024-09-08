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

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${security.oauth2.client.client-id}")
    private String clientIdOauth2;

    @Value("${security.oauth2.client.client-secret}")
    private  String clientSecretOauth2;

    @Value("${jwt.duration}")
    private Integer jwtDuration;


    @Value("${cors.origins}")
    private String corsOrigins;

    @Value("${boleto.numerocontacorrente}")
    private String numeroContaCorrente;

    @Value("${source.docker}")
    private boolean isDocker;

}
