package com.inter.boletos.client.service;

import com.inter.boletos.client.dto.FiltroListagemBoletoDTO;
import com.inter.boletos.client.dto.boleto.*;
import com.inter.boletos.client.dto.token.TokenResponseDTO;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class BoletoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoletoService.class);
    private static final BoletoService INSTANCE = new BoletoService();




    private BoletoService() {

    }

    public static BoletoService getInstance() {
        return INSTANCE;
    }


    public TokenResponseDTO devolvetoken (RequestBoleto requestBoleto){
        return TokenService.getInstance().getToken( requestBoleto);
    }

    public ResponseListagemBoletosDTO listaBoletos(FiltroListagemBoletoDTO filtro, RequestBoleto requestBoleto) throws Exception {
        TokenResponseDTO token = TokenService.getInstance().getToken( requestBoleto);
        LOGGER.info("TESTE DETALHE  URL: {} ", requestBoleto.getUrlBancoInterBoleto());
        LOGGER.info("TESTE DETALHE  Token: {} ", "Bearer " + token.getAccess_token());
        LOGGER.info("TESTE DETALHE  Data Inicial: {} ", filtro.getDataInicial());
        LOGGER.info("TESTE DETALHE  Data Final: {} ", filtro.getDataFinal());
        HttpResponse<ResponseListagemBoletosDTO> response = Unirest.get(requestBoleto.getUrlBancoInterBoleto())
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token.getAccess_token())
                .header("Host","cdpj.partners.bancointer.com.br")
                .queryString("dataInicial", filtro.getDataInicial())
                .queryString("dataFinal", filtro.getDataFinal())
                .asObject(ResponseListagemBoletosDTO.class);


        LOGGER.info("BODY : {}", response.getBody());
        return response.getBody();
    }

    public ResponseBoletoDetalheDTO boletoDetalhado(FiltroListagemBoletoDTO filtro, RequestBoleto requestBoleto) throws Exception {
        TokenResponseDTO token = TokenService.getInstance().getToken( requestBoleto);
        String url = requestBoleto.getUrlBancoInterBoleto() +  "/{nossoNumero}";
        LOGGER.info("TESTE DETALHE  URL: {} ", url);

        HttpResponse<ResponseBoletoDetalheDTO> response = Unirest.get(url)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token.getAccess_token())
                .header("x-conta-corrente", filtro.getNumConta())
                .header("Host","cdpj.partners.bancointer.com.br")
                .routeParam("nossoNumero", filtro.getNossoNumero())
                .asObject(ResponseBoletoDetalheDTO.class);

        LOGGER.info("BODY : {}", response.getBody());
        return response.getBody();
    }

    public ResponseBoletoDTO geraBoleto(RequestBoleto requestBoleto, BoletoDTO boletoDTO) throws Exception {
        TokenResponseDTO token = new TokenResponseDTO();

        while (Objects.isNull(token.getAccess_token())){
            token = TokenService.getInstance().getToken( requestBoleto);
        }
        String url = requestBoleto.getUrlBancoInterBoleto();
        LOGGER.info("TESTE DETALHE  URL: {} ", url);
        HttpResponse<ResponseBoletoDTO> response = Unirest.post(url)
                .header("Accept", "application/json")
                .header("Host","cdpj.partners.bancointer.com.br")
                .header("Content-Type", "application/json" )
                .header("Authorization", "Bearer " + token.getAccess_token())
                .body(boletoDTO)
                .asObject(ResponseBoletoDTO.class);

        LOGGER.info("BODY : {}", response.getBody());
        return response.getBody();
    }

    public String cancelaBoleto(FiltroListagemBoletoDTO filtro, RequestBoleto requestBoleto) throws Exception {
        TokenResponseDTO token = TokenService.getInstance().getToken( requestBoleto);
        String url = requestBoleto.getUrlBancoInterBoleto() +  "/{nossoNumero}/cancelar";
        LOGGER.info("TESTE DETALHE  URL: {} ", url);
        HttpResponse<String> response = Unirest.post(url)
                .header("Accept", "application/json")
                .header("Host","cdpj.partners.bancointer.com.br")
                .header("Content-Type", "application/json" )
                .header("Authorization", "Bearer " + token.getAccess_token())
                //.header("x-conta-corrente", filtro.getNumConta())
                .routeParam("nossoNumero", filtro.getNossoNumero())
                .body(filtro)
                .asString();
                //.asObject(ResponseBoletoDTO.class);

        LOGGER.info("BODY : {}", response.getBody());
        return response.getBody();
    }

    public BoletoPDFDto downloadPDF(FiltroListagemBoletoDTO filtro, RequestBoleto requestBoleto) throws Exception {
        TokenResponseDTO token = TokenService.getInstance().getToken( requestBoleto);
        String url = requestBoleto.getUrlBancoInterBoleto() +  "/{nossoNumero}/pdf";
        LOGGER.info("TESTE DETALHE  URL: {} ", url);
        HttpResponse<BoletoPDFDto> response = Unirest.get(url)
                .header("Accept", "application/json")
                .header("Host","cdpj.partners.bancointer.com.br")
                .header("Content-Type", "application/json" )
                .header("Authorization", "Bearer " + token.getAccess_token())
                //.header("x-conta-corrente", filtro.getNumConta())
                .routeParam("nossoNumero", filtro.getNossoNumero())
               // .asString();
        .asObject(BoletoPDFDto.class);

        LOGGER.info("BODY : {}", response.getBody());
        return response.getBody();
    }

}
