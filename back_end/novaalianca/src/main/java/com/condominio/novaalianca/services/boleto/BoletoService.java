package com.condominio.novaalianca.services.boleto;


import com.condominio.novaalianca.builder.BoletoBuilder;
import com.condominio.novaalianca.builder.UsuarioBuilder;
import com.condominio.novaalianca.dto.boleto.BoletoDTO;
import com.condominio.novaalianca.dto.boleto.BoletoEmissaoDTO;
import com.condominio.novaalianca.dto.boleto.BoletoTESTEOLDDTO;
import com.condominio.novaalianca.dto.boleto.BoletoPDFDto;
import com.condominio.novaalianca.dto.boleto.ContentDTO;
import com.condominio.novaalianca.dto.boleto.FiltroListagemBoletoDTO;
import com.condominio.novaalianca.dto.boleto.RequestBoleto;
import com.condominio.novaalianca.dto.boleto.ResponseBoletoDTO;
import com.condominio.novaalianca.dto.boleto.ResponseBoletoDetalheDTO;
import com.condominio.novaalianca.dto.boleto.ResponseListagemBoletosDTO;
import com.condominio.novaalianca.dto.token.TokenResponseDTO;
import com.condominio.novaalianca.entities.Boleto;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.BoletoRepository;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

@Service
public class BoletoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoletoService.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioBuilder usuarioBuilder;

    @Autowired
    private BoletoRepository boletoRepository;


    @Autowired
    private BoletoBuilder boletoBuilder;




    public TokenResponseDTO devolvetoken (RequestBoleto requestBoleto){
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

    public ResponseBoletoDTO geraBoleto(RequestBoleto requestBoleto, BoletoEmissaoDTO boletoDTO) throws Exception {
        TokenResponseDTO token = new TokenResponseDTO();
        boolean execute = false;
        int count = 0;
        while (Objects.isNull(token.getAccess_token()) &&  count < 15){
            token = tokenService.getToken( requestBoleto);
            count++;
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
        TokenResponseDTO token = tokenService.getToken( requestBoleto);
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

    public BoletoPDFDto downloadPDF(String nossoNumero, RequestBoleto requestBoleto) throws Exception {
        TokenResponseDTO token = tokenService.getToken( requestBoleto);
        String url = requestBoleto.getUrlBancoInterBoleto() +  "/{nossoNumero}/pdf";
        LOGGER.info("TESTE DETALHE  URL: {} ", url);
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
        DateTimeFormatter formatterDataHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatterDataHora1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        DateTimeFormatter formatterData1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (ContentDTO dto : responseListagemBoletosDTO.getContent()){

            Usuario usuario = usuarioBuilder.byCPF(dto.getPagador().getCpfCnpj());
            Boleto boleto = new Boleto();
            LOGGER.info("dthoRA {}",dto.getDataHoraSituacao());
            boleto.setDhSituacao(LocalDateTime.parse(dto.getDataHoraSituacao(),formatterDataHora1));
            boleto.setDtBaixa(null);
            boleto.setDtEmissao(LocalDate.parse(dto.getDataEmissao().format(formatterData1),formatterData1));
            boleto.setDtEnvio(null);
            boleto.setDtLimitePagamento(dto.getDataLimite());
            boleto.setDtPagamento(LocalDate.parse(dto.getDataHoraSituacao(), formatterDataHora1));
            boleto.setDtVencimento(LocalDate.parse(dto.getDataVencimento().format(formatterData1),formatterData1));
            boleto.setMotivoBaixa(null);
            boleto.setNossoNumero(dto.getNossoNumero());
            boleto.setSeuNumero(dto.getSeuNumero());
            boleto.setTxCancelamento(null);
            boleto.setTxCodBarras(dto.getCodigoBarras());
            boleto.setTxEspecie(dto.getCodigoEspecie());
            boleto.setTxLinhaDigitavel(dto.getLinhaDigitavel());
            boleto.setTxOrigem(dto.getOrigem());
            boleto.setTxSituacao(dto.getSituacao());
            boleto.setValor(dto.getValorNominal().doubleValue());
            boleto.setValorPagamento(Objects.isNull(dto.getValorTotalRecebimento())? 0 : dto.getValorTotalRecebimento().doubleValue());
            boleto.setEmpresa(null);
            boleto.setIdUnidade(usuario.getUnidade());
            boleto.setUsuario(usuario);
            LOGGER.info("Boleto {}", boleto.toString());
            boletoRepository.save(boleto);
            LOGGER.info("Boleto Salvo, Inquilino {}, Mes {}", usuario.getNomeUsuario(), dto.getDataEmissao().getMonth().toString());

        }
        return "Deu Bom";
    }

    public Page<BoletoDTO> findAllPaged(Pageable pageable) {
        Page<Boleto> list = boletoRepository.findAll(pageable);
        return list.map(x -> boletoBuilder.entityToDTO(x));
    }

    public BoletoDTO findByNossoNumero(String nossoNumero) {
        Boleto boleto = boletoRepository.findByNossoNumero(nossoNumero);
        return boletoBuilder
                .entityToDTO(boleto);

    }

    public Page<BoletoDTO> findAllPagedByCpfUsuario(Pageable pageable, String cpfUsuario) {
        Page<Boleto> list = boletoRepository.findAllbyCpfUsuario(pageable,cpfUsuario);
        return list.map(x -> boletoBuilder.entityToDTO(x));
    }

    public Page<BoletoDTO> findAllPagedByIdUsuario(Pageable pageable, Long idUsuario) {
        Page<Boleto> list = boletoRepository.findAllbyIdUsuario(pageable,idUsuario);
        return list.map(x -> boletoBuilder.entityToDTO(x));
    }
}
