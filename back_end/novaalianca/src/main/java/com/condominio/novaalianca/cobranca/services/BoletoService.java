package com.condominio.novaalianca.cobranca.services;


import com.condominio.novaalianca.cobranca.builder.BoletoBuilder;
import com.condominio.novaalianca.builder.PagingDTOBuilder;
import com.condominio.novaalianca.builder.RequestBoletoBuilder;
import com.condominio.novaalianca.builder.UsuarioBuilder;
import com.condominio.novaalianca.cobranca.models.dto.ResponseCobrancaDTO;
import com.condominio.novaalianca.dto.EmailDTO;
import com.condominio.novaalianca.cobranca.models.dto.BoletoDTO;
import com.condominio.novaalianca.dto.boleto.BoletoEmissaoDTO;
import com.condominio.novaalianca.dto.boleto.BoletoPDFDto;
import com.condominio.novaalianca.cobranca.models.dto.ContentDTO;
import com.condominio.novaalianca.dto.boleto.FiltroListagemBoletoDTO;
import com.condominio.novaalianca.dto.boleto.RequestBoleto;
import com.condominio.novaalianca.dto.boleto.ResponseBoletoDTO;
import com.condominio.novaalianca.dto.boleto.ResponseBoletoDetalheDTO;
import com.condominio.novaalianca.dto.boleto.ResponseListagemBoletosDTO;
import com.condominio.novaalianca.dto.pageable.PageableResponseDTO;
import com.condominio.novaalianca.dto.token.TokenResponseDTO;
import com.condominio.novaalianca.entities.BoletoNovaAlianca;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.cobranca.repositories.BoletoRepository;
import com.condominio.novaalianca.services.EmailService;
import com.condominio.novaalianca.util.DateUtils;
import inter.cobranca.model.Boleto;
import inter.cobranca.model.BoletoDetalhado;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BoletoService{

    private static final Logger LOGGER = LoggerFactory.getLogger(BoletoService.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioBuilder usuarioBuilder;

    @Autowired
    private BoletoRepository boletoRepository;


    @Autowired
    private BoletoBuilder boletoBuilder;

    @Autowired
    private DateUtils dateUtils;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RequestBoletoBuilder builder;

    public TokenResponseDTO devolvetoken (RequestBoleto requestBoleto) throws IOException {
        return tokenService.getToken(requestBoleto);
    }

    public ResponseListagemBoletosDTO listaBoletos(String dataInicio, String dataFim, RequestBoleto requestBoleto) throws Exception {
        TokenResponseDTO token = tokenService.getToken( requestBoleto);
        LOGGER.info("URL: {} ", requestBoleto.getUrlBancoInterBoleto());
        LOGGER.info("Token: {} ", "Bearer " + token.getAccess_token());
        LOGGER.info("Data Inicial: {} ", dataInicio);
        LOGGER.info("Data Final: {} ", dataFim);
        HttpResponse<ResponseListagemBoletosDTO> response = Unirest.get(requestBoleto.getUrlBancoInterBoleto())
//        HttpResponse<ResponseListagemBoletosDTO> response = Unirest.get("https://cdpj.partners.bancointer.com.br/cobranca/v3/cobrancas?dataInicial=2024-08-01&dataFinal=2025-06-30")
//        HttpResponse<String> response = Unirest.get("https://cdpj.partners.bancointer.com.br/cobranca/v3/cobrancas?dataInicial=2024-08-01&dataFinal=2025-06-30")
//        HttpResponse<String> response1 = Unirest.get(requestBoleto.getUrlBancoInterBoleto())
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token.getAccess_token())
//                .header("Host","cdpj.partners.bancointer.com.br")
                .queryString("dataInicial", dataInicio)
                .queryString("dataFinal", dataFim)
                .asObject(ResponseListagemBoletosDTO.class);
//                .asString();
        LOGGER.info("Response: {} ", response.getBody());
//        Gson gson = new Gson();
//        ResponseListagemBoletosDTO response = gson.fromJson(response1.getBody(), ResponseListagemBoletosDTO.class);
        //        HttpResponse<String> response = Unirest.get("https://cdpj.partners.bancointer.com.br/cobranca/v3/cobrancas?dataInicial=2024-08-01&dataFinal=2025-06-30")
//                .header("Accept", "application/json")
//                .header("Authorization", "Bearer cbb8f88f-7db0-426a-a8a7-8526ffd5d2a8")
//                .asString();

//        LOGGER.info("BODY : {}", response.getBody().getSize());
        return response.getBody();
    }

//    public ResponseBoletoDetalheDTO boletoDetalhado(FiltroListagemBoletoDTO filtro, RequestBoleto requestBoleto) throws Exception {
//        TokenResponseDTO token = tokenService.getToken( requestBoleto);
//        String url = requestBoleto.getUrlBancoInterBoleto() +  "/{nossoNumero}";
//
//        HttpResponse<ResponseBoletoDetalheDTO> response = Unirest.get(url)
//                .header("Accept", "application/json")
//                .header("Authorization", "Bearer " + token.getAccess_token())
//                .header("x-conta-corrente", filtro.getNumConta())
//                .header("Host","cdpj.partners.bancointer.com.br")
//                .routeParam("nossoNumero", filtro.getNossoNumero())
//                .asObject(ResponseBoletoDetalheDTO.class);
//
//        LOGGER.info("BODY : {}", response.getBody());
//        return response.getBody();
//    }
//
//    public ResponseBoletoDTO geraBoleto(RequestBoleto requestBoleto, BoletoEmissaoDTO boletoDTO) throws Exception {
//        TokenResponseDTO token = new TokenResponseDTO();
//        boolean execute = false;
//        int count = 0;
//        while (Objects.isNull(token.getAccess_token()) &&  count < 15){
//            token = tokenService.getToken( requestBoleto);
//            count++;
//        }
//        String url = requestBoleto.getUrlBancoInterBoleto();
//        HttpResponse<ResponseBoletoDTO> response = Unirest.post(url)
//                .header("Accept", "application/json")
//                .header("Host","cdpj.partners.bancointer.com.br")
//                .header("Content-Type", "application/json" )
//                .header("Authorization", "Bearer " + token.getAccess_token())
//                .body(boletoDTO)
//                .asObject(ResponseBoletoDTO.class);
//
//        LOGGER.info("BODY : {}", response.getBody());
//        return response.getBody();
//    }
//
//    public String cancelaBoleto(FiltroListagemBoletoDTO filtro, RequestBoleto requestBoleto) throws Exception {
//        TokenResponseDTO token = tokenService.getToken( requestBoleto);
//        String url = requestBoleto.getUrlBancoInterBoleto() +  "/{nossoNumero}/cancelar";
//        HttpResponse<String> response = Unirest.post(url)
//                .header("Accept", "application/json")
//                .header("Host","cdpj.partners.bancointer.com.br")
//                .header("Content-Type", "application/json" )
//                .header("Authorization", "Bearer " + token.getAccess_token())
//                //.header("x-conta-corrente", filtro.getNumConta())
//                .routeParam("nossoNumero", filtro.getNossoNumero())
//                .body(filtro)
//                .asString();
//                //.asObject(ResponseBoletoDTO.class);
//
//        LOGGER.info("BODY : {}", response.getBody());
//        return response.getBody();
//    }

    public BoletoPDFDto downloadPDF(String codSolicitacao, RequestBoleto requestBoleto) throws Exception {
        TokenResponseDTO token = tokenService.getToken( requestBoleto);
        String url = requestBoleto.getUrlBancoInterBoleto() +  "/{codSolicitacao}/pdf";
        HttpResponse<BoletoPDFDto> response = Unirest.get(url)
                .header("Accept", "application/json")
                .header("Host","cdpj.partners.bancointer.com.br")
                .header("Content-Type", "application/json" )
                .header("Authorization", "Bearer " + token.getAccess_token())
                //.header("x-conta-corrente", filtro.getNumConta())
                .routeParam("codSolicitacao", codSolicitacao)
               // .asString();
        .asObject(BoletoPDFDto.class);

        LOGGER.info("BODY : {}", response.getBody());
        return response.getBody();
    }

    public ResponseListagemBoletosDTO cargaBoleo(String dataInicio, String datafim, RequestBoleto requestBoleto) throws Exception {

        ResponseListagemBoletosDTO responseListagemBoletosDTO = this.listaBoletos(dataInicio,datafim, requestBoleto);

        if(Objects.isNull(responseListagemBoletosDTO.getCobrancas()) || responseListagemBoletosDTO.getCobrancas().isEmpty()){
            LOGGER.info("Lista Vazia");

            return responseListagemBoletosDTO;
        }


        for (ResponseCobrancaDTO contentDTO : responseListagemBoletosDTO.getCobrancas().stream().collect(Collectors.toList())){
            BoletoNovaAlianca boletoEntity = boletoRepository.findByTxCodBarras(contentDTO.getBoleto().getCodigoBarras());

            if(!Objects.isNull(boletoEntity ) && (!contentDTO.getCobranca().getSituacao().equals(boletoEntity.getTxSituacao())
                    || !contentDTO.getCobranca().getCodigoSolicitacao().equals(boletoEntity.getCodSolicitacao()))){
                boletoRepository.save(boletoBuilder.updateBoletoCarga(boletoEntity,contentDTO));
            }else if(Objects.isNull(boletoEntity)){

                boletoRepository.save(boletoBuilder.newBoletoCarga(contentDTO));


            }
        }
        LOGGER.info("Resultado Listagem Boletos {}" , responseListagemBoletosDTO);
        return responseListagemBoletosDTO;
    }

////    public PageableResponseDTO<BoletoDTO> findAllPaged(Pageable pageable) {
////        final Page<BoletoNovaAlianca> boletoEntitie = boletoRepository.findAll(pageable);
////
////        final List<BoletoDTO> boletoDTOSet = boletoEntitie.get().map(x->boletoBuilder.entityToDTO(x)).collect(Collectors.toList());
////
////        return buildPageableResponseDTO(boletoEntitie, boletoDTOSet);
////
////    }
//
////    public BoletoDTO findByNossoNumero(String nossoNumero) {
////        BoletoNovaAlianca boletoNovaAlianca = boletoRepository.findByNossoNumero(nossoNumero);
////        return boletoBuilder
////                .entityToDTO(boletoNovaAlianca);
////
////    }
//
//    public Page<BoletoDTO> findAllPagedByCpfUsuario(Pageable pageable, String cpfUsuario) {
//        Page<BoletoNovaAlianca> list = boletoRepository.findAllbyCpfUsuario(pageable,cpfUsuario);
//        return list.map(x -> boletoBuilder.entityToDTO(x));
//    }
//
//    public PageableResponseDTO<BoletoDTO> findAllPagedByIdUsuario(Pageable pageable, Long idUsuario) {
//        Page<BoletoNovaAlianca> list = boletoRepository.findAllbyIdUsuario(pageable,idUsuario);
//
//        final List<BoletoDTO> boletoDTOSet = list.get().map(x->boletoBuilder.entityToDTO(x)).collect(Collectors.toList());
//
//        return buildPageableResponseDTO(list, boletoDTOSet);
//
//
//
//    }
//
//    public List<BoletoNovaAlianca> validaBoletosEnviadosMes(String month) {
//        return boletoRepository.findAllByMesEmissao(month);
//    }
//
    public Boleto builderBoletoInter(Usuario usuario) throws ParseException {
        return boletoBuilder.boletoInter(usuario);
    }
//
//    public void save(BoletoDetalhado boleto) {
//
//        boletoRepository.save(boletoBuilder.entityInterToEntityNovaAlianca(boleto));
//    }
//
    public void enviaBoletosPorEmail(LocalDate dtInicio, LocalDate dtFim) throws Exception {

        LOGGER.info("MES ATUAL {}", dateUtils.mesAtual());
        List<BoletoNovaAlianca> list = boletoRepository.findAllByMesEmissaoAndNaoEnviadoByEmail(dtInicio,dtFim);

        if(list.size()>0){
            EmailDTO emailDTO = new EmailDTO();
            LOGGER.info("Enviando Boleto Para {}" , list.get(0).getUsuario().getNomeUsuario());
            emailDTO.setNossoNumero(list.get(0).getNossoNumero());
            byte[] decoder = Base64.getDecoder().decode(this.downloadPDF( list.get(0).getCodSolicitacao(), builder.requestBoleto("boleto-cobranca.read")).getPdf());

            emailDTO.setAnexo(decoder);

            emailDTO.setNumeroUnidade(list.get(0).getUsuario().getUnidade().getNumeroUnidade());
            emailDTO.setTo(list.get(0).getUsuario().getTxEmail());
            emailService.sendMail(emailDTO);

            list.get(0).setEmailEnviado(Boolean.TRUE);
            boletoRepository.save(list.get(0));
            LOGGER.info("Boleto enviado Para {}" , list.get(0).getUsuario().getNomeUsuario());

        }else {
            LOGGER.info("Sem Emails para Enviar");
        }
    }
//
//    private PageableResponseDTO<BoletoDTO> buildPageableResponseDTO(Page<BoletoNovaAlianca> page, List<BoletoDTO> boletoDTOSet) {
//        PageableResponseDTO<BoletoDTO> responseDTO = new PageableResponseDTO<>();
//
//        responseDTO.setPaging(PagingDTOBuilder.from(page));
//        responseDTO.setContent(boletoDTOSet);
//
//        return responseDTO;
//    }
}
