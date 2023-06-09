package com.condominio.novaalianca.services.boleto;

import com.condominio.novaalianca.dto.boleto.RequestBoleto;
import com.condominio.novaalianca.dto.token.TokenResponseDTO;
import com.google.gson.Gson;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);

    public TokenResponseDTO getToken (RequestBoleto requestBoleto){
        Gson gson = new Gson();
        String caminhoArquivo = getClass().getResource("../").toString();

        String webDir = "novaalianca/";
        caminhoArquivo = caminhoArquivo.substring(6, caminhoArquivo.indexOf(webDir)+webDir.length());
        LOGGER.info("Caminho do certificado {}", requestBoleto.getCaminhoCertificado());
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
