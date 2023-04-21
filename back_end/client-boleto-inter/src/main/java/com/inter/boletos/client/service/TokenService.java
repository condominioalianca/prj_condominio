package com.inter.boletos.client.service;

import com.google.gson.Gson;
import com.inter.boletos.client.dto.boleto.RequestBoleto;
import com.inter.boletos.client.dto.token.TokenResponseDTO;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);
    private static final TokenService INSTANCE = new TokenService();

     public static TokenService getInstance() {
        return INSTANCE;
    }

    public TokenResponseDTO getToken (RequestBoleto requestBoleto){
        Gson gson = new Gson();
        String caminhoArquivo = getClass().getResource("../").toString();

        String webDir = "client-boleto-inter/";
        caminhoArquivo = caminhoArquivo.substring(6, caminhoArquivo.indexOf(webDir)+webDir.length());
//        LOGGER.info("Caminho do certificado {}", requestBoleto.getCaminhoCertificado());
        caminhoArquivo = caminhoArquivo + requestBoleto.getCaminhoCertificado();

        caminhoArquivo = caminhoArquivo.replace("/", "//");
//        LOGGER.info("Caminho do Arquivo {}", caminhoArquivo);
        Unirest.config().clientCertificateStore(caminhoArquivo , requestBoleto.getSenhaCertificado());
        HttpResponse<TokenResponseDTO> response = Unirest.post(requestBoleto.getUrlBancoInterToken())
                .multiPartContent()
                .field("client_id", requestBoleto.getClientId())
                .field("client_secret", requestBoleto.getClientIdSecret())
                .field("grant_type", "client_credentials")
                .field("scope", requestBoleto.getScope())
                .asObject(TokenResponseDTO.class);
        LOGGER.info("Response : {}", gson.toJson(response.getBody()));
        return response.getBody();
    }
}
