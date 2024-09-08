package com.condominio.novaalianca.services.boleto;

import com.condominio.novaalianca.dto.boleto.RequestBoleto;
import com.condominio.novaalianca.dto.token.TokenResponseDTO;
import com.condominio.novaalianca.util.CaminhoArquivosUtil;
import com.google.gson.Gson;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TokenService {

    @Autowired
    private CaminhoArquivosUtil caminhoArquivosUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);

    public TokenResponseDTO getToken (RequestBoleto requestBoleto) throws IOException {
        Gson gson = new Gson();

       String  caminhoArquivo = caminhoArquivosUtil.caminhoCertificado();
       LOGGER.info("Caminho do Arquivo {}", caminhoArquivo);

//        Resource resource = new ClassPathResource("./certs/CONDOMINIONOVAALIANCA.pfx");
//        String caminhoArquivoResource = resource.getFile().getPath();
//        caminhoArquivoResource = caminhoArquivoResource.replace("/", "//");
//
//
//        LOGGER.info("Caminho do ArquivoResource {}", caminhoArquivoResource);
//        LOGGER.info("Caminho do URLArquivoResource {}", resource.getURI().getPath());
//        LOGGER.info("Caminho do URLArquivoResourceFragment {}", resource.getURI().getRawFragment());
//        LOGGER.info("Caminho do ArquivoResourceDescription {}", resource.getDescription());
//        LOGGER.info("Caminho do ArquivoResourceFileName {}", resource.getFilename());


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
