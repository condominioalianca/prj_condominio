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
import com.condominio.novaalianca.services.exceptions.ResourceNotFoundException;
import com.condominio.novaalianca.util.DateUtils;
import com.condominio.novaalianca.dto.inter.cobranca.Boleto;
import com.condominio.novaalianca.services.inter.InterService;
import com.condominio.novaalianca.dto.inter.cobranca.Boletoenriquecido;
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
import java.util.NoSuchElementException;
import java.util.Objects;
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

    @Autowired
    private InterService interService;

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

    public String downloadPDF(String codSolicitacao, String ambiente) throws Exception {
        if (ambiente == null || ambiente.trim().isEmpty()) {
            ambiente = "SANDBOX";
        }

        // 1. Chamar o InterService para obter o PDF
        BoletoPDFDto pdfDto = interService.obterBoletoPdf(codSolicitacao, null, ambiente);
        if (pdfDto == null || pdfDto.getPdf() == null) {
            throw new RuntimeException("Não foi possível baixar o PDF do Banco Inter.");
        }

        // 2. Localizar o boleto no banco de dados local pelo codSolicitacao
        BoletoNovaAlianca boleto = boletoRepository.findByCodSolicitacao(codSolicitacao);
        if (boleto != null) {
            // Decodificar o Base64 e persistir no campo Lob
            byte[] pdfBytes = Base64.getDecoder().decode(pdfDto.getPdf());
            boleto.setArquivopdf(pdfBytes);
            boletoRepository.save(boleto);
            LOGGER.info("PDF do boleto com código de solicitação {} salvo com sucesso no banco local.", codSolicitacao);
        }

        // 3. Retornar a string base64
        return pdfDto.getPdf();
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
            byte[] decoder = list.get(0).getArquivopdf();
            if (decoder == null || decoder.length == 0) {
                LOGGER.info("PDF não encontrado localmente. Buscando no Banco Inter para o código: {}", list.get(0).getCodSolicitacao());
                decoder = Base64.getDecoder().decode(this.downloadPDF(list.get(0).getCodSolicitacao(), "PRODUCAO"));
            } else {
                LOGGER.info("PDF do boleto carregado diretamente da base local.");
            }

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

    public BoletoNovaAlianca enriquecerBoleto(String codigoSolicitacao, String ambiente) {
        if (codigoSolicitacao == null || codigoSolicitacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Código de solicitação inválido.");
        }

        if (ambiente == null || ambiente.trim().isEmpty()) {
            ambiente = "SANDBOX";
        }

        // 1. Buscar o boleto local correspondente
        BoletoNovaAlianca boletoLocal = boletoRepository.findByCodSolicitacao(codigoSolicitacao);
        if (boletoLocal == null) {
            throw new NoSuchElementException("Boleto local não encontrado para o código de solicitação: " + codigoSolicitacao);
        }

        // 2. Chamar o interService para buscar os dados detalhados/enriquecidos
        Boletoenriquecido response = interService.getBoletoEnriquecido(codigoSolicitacao,ambiente   );
        if (response == null || response.getCobranca() == null) {
            throw new RuntimeException("Não foi possível recuperar os dados detalhados do Banco Inter para o boleto.");
        }

        // 3. Validações
        // Validar se o codigoSolicitacao é o mesmo
        String apiCodSolicitacao = response.getCobranca().getCodigoSolicitacao();
        if (!codigoSolicitacao.equalsIgnoreCase(apiCodSolicitacao)) {
            throw new IllegalStateException("O código de solicitação retornado pela API (" + apiCodSolicitacao + ") difere do solicitado (" + codigoSolicitacao + ").");
        }

        // Validar se o CPF do pagador recuperado é o mesmo que temos no usuário da base
        if (response.getCobranca().getPagador() == null) {
            throw new IllegalStateException("Os dados do pagador não foram retornados pela API do Banco Inter.");
        }
        if (boletoLocal.getUsuario() == null) {
            throw new IllegalStateException("O boleto local não possui um usuário pagador associado.");
        }

        String apiCpfCnpj = response.getCobranca().getPagador().getCpfCnpj().replaceAll("[^0-9]", "");
        String localCpfCnpj = boletoLocal.getUsuario().getNrDocumentoCpf().replaceAll("[^0-9]", "");

        if (!apiCpfCnpj.equals(localCpfCnpj)) {
            throw new IllegalArgumentException("Validação falhou: o CPF do pagador retornado (" + apiCpfCnpj + ") não coincide com o do usuário na base (" + localCpfCnpj + ").");
        }

        // 4. Mapear e Enriquecer os dados do boleto
        // Nosso Número
        if (response.getBoleto() != null) {
            if (response.getBoleto().getNossoNumero() != null) {
                boletoLocal.setNossoNumero(response.getBoleto().getNossoNumero());
            }
            if (response.getBoleto().getCodigoBarras() != null) {
                boletoLocal.setTxCodBarras(response.getBoleto().getCodigoBarras());
            }
            if (response.getBoleto().getLinhaDigitavel() != null) {
                boletoLocal.setTxLinhaDigitavel(response.getBoleto().getLinhaDigitavel());
            }
        }

        // Situação e outras informações da cobrança
        if (response.getCobranca().getSituacao() != null) {
            boletoLocal.setTxSituacao(response.getCobranca().getSituacao());
        }

        // Origem do recebimento
        if (response.getCobranca().getOrigemRecebimento() != null) {
            boletoLocal.setTxOrigem(response.getCobranca().getOrigemRecebimento());
        }

        // Datas de baixa/pagamento/situação
        if (response.getCobranca().getDataSituacao() != null) {
            try {
                LocalDate dataSituacao = LocalDate.parse(response.getCobranca().getDataSituacao());
                boletoLocal.setDhSituacao(dataSituacao.atStartOfDay());
                
                // Se a situação for RECEBIDO, preenchemos o valor pago e a data de pagamento
                if ("RECEBIDO".equalsIgnoreCase(response.getCobranca().getSituacao())) {
                    boletoLocal.setDtPagamento(dataSituacao);
                    if (response.getCobranca().getValorTotalRecebido() != null) {
                        try {
                            boletoLocal.setValorPagamento(Double.parseDouble(response.getCobranca().getValorTotalRecebido()));
                        } catch (NumberFormatException e) {
                            LOGGER.warn("Erro ao converter valor recebido: {}", response.getCobranca().getValorTotalRecebido());
                        }
                    }
                } else if ("CANCELADO".equalsIgnoreCase(response.getCobranca().getSituacao()) || "BAIXADO".equalsIgnoreCase(response.getCobranca().getSituacao())) {
                    boletoLocal.setDtBaixa(dataSituacao);
                    boletoLocal.setMotivoBaixa(response.getCobranca().getMotivoCancelamento());
                }
            } catch (Exception e) {
                LOGGER.warn("Erro ao fazer parse da dataSituacao: {}", response.getCobranca().getDataSituacao(), e);
            }
        }

        // 4.5 Buscar PDF do Banco Inter e associar à entidade antes de persistir
        try {
            LOGGER.info("Buscando PDF para anexar ao boleto de código: {}", codigoSolicitacao);
            BoletoPDFDto pdfDto = interService.obterBoletoPdf(codigoSolicitacao, null, ambiente);
            if (pdfDto != null && pdfDto.getPdf() != null) {
                byte[] pdfBytes = Base64.getDecoder().decode(pdfDto.getPdf());
                boletoLocal.setArquivopdf(pdfBytes);
                LOGGER.info("PDF anexado com sucesso para persistência conjunta.");
            }
        } catch (Exception e) {
            LOGGER.error("Falha ao recuperar e anexar o PDF do boleto durante o enriquecimento: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar o PDF do boleto para persistência conjunta: " + e.getMessage(), e);
        }

        // 5. Persistir e retornar
        return boletoRepository.save(boletoLocal);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<BoletoNovaAlianca> findAll() {
        return boletoRepository.findAll();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public BoletoNovaAlianca findById(Long id) {
        return boletoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boleto nao encontrado para o ID: " + id));
    }

    @org.springframework.transaction.annotation.Transactional
    public BoletoNovaAlianca save(BoletoNovaAlianca entity) {
        return boletoRepository.save(entity);
    }

    @org.springframework.transaction.annotation.Transactional
    public BoletoNovaAlianca update(BoletoNovaAlianca entity) {
        if (!boletoRepository.existsById(entity.getId())) {
            throw new ResourceNotFoundException("Boleto nao encontrado para o ID: " + entity.getId());
        }
        return boletoRepository.save(entity);
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteById(Long id) {
        if (!boletoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Boleto nao encontrado para o ID: " + id);
        }
        boletoRepository.deleteById(id);
    }
}
