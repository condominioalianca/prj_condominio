package com.condominio.novaalianca.cobranca.services;


import com.condominio.novaalianca.builder.BoletoBuilder;
import com.condominio.novaalianca.builder.PagingDTOBuilder;
import com.condominio.novaalianca.builder.RequestBoletoBuilder;
import com.condominio.novaalianca.builder.UsuarioBuilder;
import com.condominio.novaalianca.dto.EmailDTO;
import com.condominio.novaalianca.dto.boleto.BoletoDTO;
import com.condominio.novaalianca.dto.boleto.BoletoEmissaoDTO;
import com.condominio.novaalianca.dto.boleto.BoletoPDFDto;
import com.condominio.novaalianca.dto.boleto.ContentDTO;
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
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

    public ResponseListagemBoletosDTO listaBoletos(FiltroListagemBoletoDTO filtro, RequestBoleto requestBoleto) throws Exception {
        TokenResponseDTO token = tokenService.getToken( requestBoleto);
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
        TokenResponseDTO token = tokenService.getToken( requestBoleto);
        String url = requestBoleto.getUrlBancoInterBoleto() +  "/{nossoNumero}";

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

    public ResponseBoletoDTO geraBoleto(RequestBoleto requestBoleto, BoletoEmissaoDTO boletoDTO) throws Exception {
        TokenResponseDTO token = new TokenResponseDTO();
        boolean execute = false;
        int count = 0;
        while (Objects.isNull(token.getAccess_token()) &&  count < 15){
            token = tokenService.getToken( requestBoleto);
            count++;
        }
        String url = requestBoleto.getUrlBancoInterBoleto();
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
        TokenResponseDTO token = tokenService.getToken( requestBoleto);
        String url = requestBoleto.getUrlBancoInterBoleto() +  "/{nossoNumero}/cancelar";
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

    public BoletoPDFDto downloadPDF(String nossoNumero, RequestBoleto requestBoleto) throws Exception {
        TokenResponseDTO token = tokenService.getToken( requestBoleto);
        String url = requestBoleto.getUrlBancoInterBoleto() +  "/{nossoNumero}/pdf";
        HttpResponse<BoletoPDFDto> response = Unirest.get(url)
                .header("Accept", "application/json")
                .header("Host","cdpj.partners.bancointer.com.br")
                .header("Content-Type", "application/json" )
                .header("Authorization", "Bearer " + token.getAccess_token())
                //.header("x-conta-corrente", filtro.getNumConta())
                .routeParam("nossoNumero", nossoNumero)
               // .asString();
        .asObject(BoletoPDFDto.class);

        LOGGER.info("BODY : {}", response.getBody());
        return response.getBody();
    }

    public String cargaBoleo(String dataInicio, String datafim, RequestBoleto requestBoleto) throws Exception {
        FiltroListagemBoletoDTO filtro = new FiltroListagemBoletoDTO();
        filtro.setDataInicial(dataInicio);
        filtro.setDataFinal(datafim);
        ResponseListagemBoletosDTO responseListagemBoletosDTO = this.listaBoletos(filtro, requestBoleto);
        if(Objects.isNull(responseListagemBoletosDTO.getContent()) || responseListagemBoletosDTO.getContent().isEmpty()){
            LOGGER.info("Lista Vazia");

            return "Lista Vazia";
        }
        List<ContentDTO> listContentDTO = responseListagemBoletosDTO.getContent().stream().collect(Collectors.toList());

        for (ContentDTO contentDTO : responseListagemBoletosDTO.getContent().stream().collect(Collectors.toList())){
            BoletoNovaAlianca boletoEntity = boletoRepository.findByTxCodBarras(contentDTO.getCodigoBarras());
            if(!Objects.isNull(boletoEntity) && boletoEntity.getTxCodBarras().equals(contentDTO.getCodigoBarras())
                    && (!contentDTO.getSituacao().equals(boletoEntity.getTxSituacao()))){
                boletoRepository.save(boletoBuilder.updateBoletoCarga(boletoEntity,contentDTO));
            }else if(Objects.isNull(boletoEntity)){

                boletoRepository.save((boletoBuilder.newBoletoCarga(contentDTO)));


            }
        }

        if(Boolean.TRUE){
            return "deu bom";
        }
        return "Deu Bom";
    }

    public PageableResponseDTO<BoletoDTO> findAllPaged(Pageable pageable) {
        final Page<BoletoNovaAlianca> boletoEntitie = boletoRepository.findAll(pageable);

        final List<BoletoDTO> boletoDTOSet = boletoEntitie.get().map(x->boletoBuilder.entityToDTO(x)).collect(Collectors.toList());

        return buildPageableResponseDTO(boletoEntitie, boletoDTOSet);

    }

    public BoletoDTO findByNossoNumero(String nossoNumero) {
        BoletoNovaAlianca boletoNovaAlianca = boletoRepository.findByNossoNumero(nossoNumero);
        return boletoBuilder
                .entityToDTO(boletoNovaAlianca);

    }

    public Page<BoletoDTO> findAllPagedByCpfUsuario(Pageable pageable, String cpfUsuario) {
        Page<BoletoNovaAlianca> list = boletoRepository.findAllbyCpfUsuario(pageable,cpfUsuario);
        return list.map(x -> boletoBuilder.entityToDTO(x));
    }

    public PageableResponseDTO<BoletoDTO> findAllPagedByIdUsuario(Pageable pageable, Long idUsuario) {
        Page<BoletoNovaAlianca> list = boletoRepository.findAllbyIdUsuario(pageable,idUsuario);

        final List<BoletoDTO> boletoDTOSet = list.get().map(x->boletoBuilder.entityToDTO(x)).collect(Collectors.toList());

        return buildPageableResponseDTO(list, boletoDTOSet);



    }

    public List<BoletoNovaAlianca> validaBoletosEnviadosMes(String month) {
        return boletoRepository.findAllByMesEmissao(month);
    }

    public Boleto builderBoletoInter(Usuario usuario) throws ParseException {
        return boletoBuilder.boletoInter(usuario);
    }

    public void save(BoletoDetalhado boleto) {

        boletoRepository.save(boletoBuilder.entityInterToEntityNovaAlianca(boleto));
    }

    public void enviaBoletosPorEmail(LocalDate dtInicio, LocalDate dtFim) throws Exception {

        LOGGER.info("MES ATUAL {}", dateUtils.mesAtual());
        List<BoletoNovaAlianca> list = boletoRepository.findAllByMesEmissaoAndNaoEnviadoByEmail(dtInicio,dtFim);

        if(list.size()>0){
            EmailDTO emailDTO = new EmailDTO();
            LOGGER.info("Enviando Boleto Para {}" , list.get(0).getUsuario().getNomeUsuario());
            emailDTO.setNossoNumero(list.get(0).getNossoNumero());
            byte[] decoder = Base64.getDecoder().decode(this.downloadPDF(emailDTO.getNossoNumero(), builder.requestBoleto("boleto-cobranca.read")).getPdf());

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

    private PageableResponseDTO<BoletoDTO> buildPageableResponseDTO(Page<BoletoNovaAlianca> page, List<BoletoDTO> boletoDTOSet) {
        PageableResponseDTO<BoletoDTO> responseDTO = new PageableResponseDTO<>();

        responseDTO.setPaging(PagingDTOBuilder.from(page));
        responseDTO.setContent(boletoDTOSet);

        return responseDTO;
    }
}
