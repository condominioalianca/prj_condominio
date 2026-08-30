package com.condominio.novaalianca.cobranca.services;


import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class BoletoService{

    private static final Logger LOGGER = LoggerFactory.getLogger(BoletoService.class);

    private final TokenService tokenService;

    private final UsuarioBuilder usuarioBuilder;

    private final BoletoRepository boletoRepository;


    private final BoletoBuilder boletoBuilder;

    private final DateUtils dateUtils;

    private final EmailService emailService;

    private final RequestBoletoBuilder builder;

    private final InterService interService;

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
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token.getAccess_token())
                .queryString("dataInicial", dataInicio)
                .queryString("dataFinal", dataFim)
                .asObject(ResponseListagemBoletosDTO.class);
        LOGGER.info("Response: {} ", response.getBody());
        return response.getBody();
    }


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

    public Boleto builderBoletoInter(Usuario usuario) throws ParseException {
        return boletoBuilder.boletoInter(usuario);
    }
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