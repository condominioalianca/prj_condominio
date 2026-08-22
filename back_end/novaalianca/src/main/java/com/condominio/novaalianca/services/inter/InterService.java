package com.condominio.novaalianca.services.inter;

import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.config.NovaAliancaProperties;
import com.condominio.novaalianca.dto.inter.banking.ExtratoResponseDTO;
import com.condominio.novaalianca.dto.inter.banking.ExtratoEnriquecidoResponseDTO;
import com.condominio.novaalianca.dto.inter.banking.SaldoResponseDTO;
import com.condominio.novaalianca.dto.inter.cobranca.Boleto;
import com.condominio.novaalianca.dto.inter.cobranca.EmissaoBoletoResponseDTO;
import com.condominio.novaalianca.dto.inter.cobranca.Boletoenriquecido;
import com.condominio.novaalianca.dto.boleto.BoletoPDFDto;
import com.condominio.novaalianca.enums.inter.TipoOperacaoEnum;
import com.condominio.novaalianca.enums.inter.TipoTransacaoEnum;
import com.condominio.novaalianca.util.RestTemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Serviço de Integração com o Banco Inter através de APIs REST nativas.
 * Delega a obtenção de token e configuração SSL para os serviços correspondentes.
 */
@Service
@RequiredArgsConstructor
public class InterService {

    private static final Logger log = LoggerFactory.getLogger(InterService.class);

    private final NovaAliancaProperties properties;

    private final InterTokenService interTokenService;

    private final RestTemplateUtil restTemplateUtil;

    /**
     * Stub para emissão de boleto (compatibilidade reversa).
     */
    public void emitirBoleto(Boleto boleto) {
        emitirBoleto(boleto, null, "PRODUCAO");
    }

    /**
     * Emite um boleto utilizando a API do Banco Inter (ambiente padrão SANDBOX).
     */
    public EmissaoBoletoResponseDTO emitirBoleto(Boleto boleto, String contaCorrente) {
        return emitirBoleto(boleto, contaCorrente, "SANDBOX");
    }

    /**
     * Emite um boleto utilizando a API do Banco Inter.
     * 
     * POST /cobranca/v3/cobrancas
     */
    public EmissaoBoletoResponseDTO emitirBoleto(Boleto boleto, String contaCorrente, String ambiente) {
        if (boleto == null) {
            throw new IllegalArgumentException("O objeto boleto não pode ser nulo.");
        }

        // Determinar a conta corrente
        if (contaCorrente == null || contaCorrente.trim().isEmpty()) {
            contaCorrente = properties.getNumeroContaCorrente();
        }

        if (contaCorrente == null || contaCorrente.trim().isEmpty()) {
            throw new IllegalArgumentException("A conta corrente não pode ser nula ou vazia.");
        }

        try {
            log.info("Iniciando emissão de boleto para o documento do pagador: {} no ambiente {}", 
                    boleto.getPagador() != null ? boleto.getPagador().getCpfCnpj() : "não informado", 
                    ambiente);

            // Obter token OAuth2 com mTLS usando o escopo de gravação de boleto
            String accessToken = interTokenService.obterAccessToken(ambiente, "boleto-cobranca.write");

            String boletoUrl = getFullUrl(properties.getBancoInterUrlBoleto(), ambiente);
            log.info("Enviando requisição de emissão de boleto para o Banco Inter em: {}", boletoUrl);

            RestTemplate restTemplate = restTemplateUtil.criarRestTemplateMtls();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("x-conta-corrente", contaCorrente);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Boleto> entity = new HttpEntity<>(boleto, headers);

            ResponseEntity<EmissaoBoletoResponseDTO> response = restTemplate.exchange(
                    boletoUrl,
                    HttpMethod.POST,
                    entity,
                    EmissaoBoletoResponseDTO.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Boleto emitido com sucesso. Código de solicitação: {}, HTTP Status: {}", 
                        response.getBody().getCodigoSolicitacao(), response.getStatusCode());
                return response.getBody();
            } else {
                log.error("Falha ao emitir boleto no Banco Inter: HTTP status {}", response.getStatusCode());
                throw new RuntimeException("Falha ao emitir boleto no Banco Inter: HTTP status " + response.getStatusCode());
            }

        } catch (HttpStatusCodeException e) {
            log.error("Erro HTTP ao emitir boleto no Banco Inter. Status: {}, Response: {}", 
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Erro HTTP na API do Banco Inter ao emitir boleto: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao emitir boleto no Banco Inter: {}", e.getMessage(), e);
            throw new RuntimeException("Erro inesperado ao emitir boleto no Banco Inter: " + e.getMessage(), e);
        }
    }

    /**
     * Consome nativamente o endpoint de extrato bancário utilizando RestTemplate com mTLS.
     * 
     * GET https://cdpj-sandbox.partners.uatinter.co/banking/v2/extrato
     */
    public ExtratoResponseDTO buscarExtrato(String dataInicio, String dataFim, String contaCorrente, String ambiente) {
        try {
            // Obter token OAuth2 com mTLS
            String accessToken = interTokenService.obterAccessToken(ambiente, "extrato.read");

            // Determinar a conta corrente
            if (contaCorrente == null || contaCorrente.trim().isEmpty()) {
                contaCorrente = properties.getNumeroContaCorrente();
            }

            String extratoUrl = getFullUrl(properties.getBancoInterUrlExtrato(), ambiente);
            log.info("Buscando extrato bancário Banco Inter em: {}", extratoUrl);

            // Monta a URL com query params
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(extratoUrl)
                    .queryParam("dataInicio", dataInicio)
                    .queryParam("dataFim", dataFim);

            RestTemplate restTemplate = restTemplateUtil.criarRestTemplateMtls();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("x-conta-corrente", contaCorrente);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<ExtratoResponseDTO> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    ExtratoResponseDTO.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Extrato recuperado com sucesso.");
                return response.getBody();
            } else {
                log.error("Erro ao buscar extrato do Banco Inter: HTTP status {}", response.getStatusCode());
                throw new RuntimeException("Erro ao buscar extrato do Banco Inter: HTTP status " + response.getStatusCode());
            }

        } catch (HttpStatusCodeException e) {
            log.error("Erro HTTP ao consumir extrato Banco Inter. Status: {}, Response: {}", 
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Erro HTTP na API do Banco Inter: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao consumir extrato Banco Inter: {}", e.getMessage(), e);
            throw new RuntimeException("Erro inesperado ao buscar extrato do Banco Inter: " + e.getMessage(), e);
        }
    }

    /**
     * Consome nativamente o endpoint de extrato enriquecido utilizando RestTemplate com mTLS.
     * 
     * GET /banking/v2/extrato/enriquecido
     */
    public ExtratoEnriquecidoResponseDTO buscarExtratoEnriquecido(
            String dataInicio,
            String dataFim,
            Integer pagina,
            Integer tamanhoPagina,
            TipoOperacaoEnum tipoOperacao,
            TipoTransacaoEnum tipoTransacao,
            String contaCorrente
    ) {
        return buscarExtratoEnriquecido(dataInicio, dataFim, pagina, tamanhoPagina, tipoOperacao, tipoTransacao, contaCorrente, null);
    }

    /**
     * Consome nativamente o endpoint de extrato enriquecido utilizando RestTemplate com mTLS (especificando ambiente).
     * 
     * GET /banking/v2/extrato/enriquecido
     */
    public ExtratoEnriquecidoResponseDTO buscarExtratoEnriquecido(
            String dataInicio,
            String dataFim,
            Integer pagina,
            Integer tamanhoPagina,
            TipoOperacaoEnum tipoOperacao,
            TipoTransacaoEnum tipoTransacao,
            String contaCorrente,
            String ambiente
    ) {
        try {
            // Obter token OAuth2 com mTLS
            String accessToken = interTokenService.obterAccessToken(ambiente, "extrato.read");

            // Determinar a conta corrente
            if (contaCorrente == null || contaCorrente.trim().isEmpty()) {
                contaCorrente = properties.getNumeroContaCorrente();
            }

            // Aplicar valor padrão de tamanhoPagina se nulo
            if (tamanhoPagina == null) {
                tamanhoPagina = 1000;
            }

            String extratoUrl = getFullUrl(properties.getBancoInterUrlExtrato() + "/completo", ambiente);
            log.info("Buscando extrato enriquecido Banco Inter em: {}", extratoUrl);

            // Monta a URL com query params obrigatórios
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(extratoUrl)
                    .queryParam("dataInicio", dataInicio)
                    .queryParam("dataFim", dataFim);

            // Adiciona query params opcionais condicionalmente
            if (pagina != null) {
                uriBuilder.queryParam("pagina", pagina);
            }
            if (tamanhoPagina != null) {
                uriBuilder.queryParam("tamanhoPagina", tamanhoPagina);
            }
            if (tipoOperacao != null) {
                uriBuilder.queryParam("tipoOperacao", tipoOperacao.getValue());
            }
            if (tipoTransacao != null) {
                uriBuilder.queryParam("tipoTransacao", tipoTransacao.name());
            }

            RestTemplate restTemplate = restTemplateUtil.criarRestTemplateMtls();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("x-conta-corrente", contaCorrente);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<ExtratoEnriquecidoResponseDTO> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    ExtratoEnriquecidoResponseDTO.class
                );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Extrato enriquecido recuperado com sucesso.");
                return response.getBody();
            } else {
                log.error("Erro ao buscar extrato enriquecido do Banco Inter: HTTP status {}", response.getStatusCode());
                throw new RuntimeException("Erro ao buscar extrato enriquecido do Banco Inter: HTTP status " + response.getStatusCode());
            }

        } catch (HttpStatusCodeException e) {
            log.error("Erro HTTP ao consumir extrato enriquecido Banco Inter. Status: {}, Response: {}", 
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Erro HTTP na API do Banco Inter: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao consumir extrato enriquecido Banco Inter: {}", e.getMessage(), e);
            throw new RuntimeException("Erro inesperado ao buscar extrato enriquecido do Banco Inter: " + e.getMessage(), e);
        }
    }

    /**
     * Consome nativamente o endpoint de saldo da conta corrente utilizando RestTemplate com mTLS.
     * 
     * GET /banking/v2/saldo
     */
    public SaldoResponseDTO buscarSaldo(String dataSaldo, String contaCorrente, String ambiente) {
        try {
            String accessToken = interTokenService.obterAccessToken(ambiente, "extrato.read");

            if (contaCorrente == null || contaCorrente.trim().isEmpty()) {
                contaCorrente = properties.getNumeroContaCorrente();
            }

            String saldoUrl = getFullUrl("/banking/v2/saldo", ambiente);
            log.info("Buscando saldo da conta corrente Banco Inter em: {}", saldoUrl);

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(saldoUrl);
            if (dataSaldo != null && !dataSaldo.trim().isEmpty()) {
                uriBuilder.queryParam("dataSaldo", dataSaldo);
            }

            RestTemplate restTemplate = restTemplateUtil.criarRestTemplateMtls();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("x-conta-corrente", contaCorrente);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<SaldoResponseDTO> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    SaldoResponseDTO.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Saldo recuperado com sucesso.");
                return response.getBody();
            } else {
                log.error("Erro ao buscar saldo do Banco Inter: HTTP status {}", response.getStatusCode());
                throw new RuntimeException("Erro ao buscar saldo do Banco Inter: HTTP status " + response.getStatusCode());
            }

        } catch (HttpStatusCodeException e) {
            log.error("Erro HTTP ao consumir saldo Banco Inter. Status: {}, Response: {}", 
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Erro HTTP na API do Banco Inter: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao consumir saldo Banco Inter: {}", e.getMessage(), e);
            throw new RuntimeException("Erro inesperado ao buscar saldo do Banco Inter: " + e.getMessage(), e);
        }
    }



    /**
     * Constrói a URL completa juntando o base path com o caminho do endpoint.
     */
    private String getFullUrl(String path, String ambiente) {
        String url = ambiente != null && !ambiente.equalsIgnoreCase("SANDBOX")
                ? properties.getBancoInterUrlPathProd() + path
                : properties.getBancoInterUrlPathSand() + path;
        return url;
    }

    public  Boletoenriquecido getBoletoEnriquecido(String codSolicitacao) {
        return getBoletoEnriquecido(codSolicitacao, null, "SANDBOX");
    }

    public Boletoenriquecido getBoletoEnriquecido(String codSolicitacao, String ambiente) {
        return getBoletoEnriquecido(codSolicitacao, null, ambiente);
    }

    public Boletoenriquecido getBoletoEnriquecido(String codSolicitacao, String contaCorrente, String ambiente) {
        if (ambiente == null || ambiente.trim().isEmpty()) {
            ambiente = "SANDBOX";
        }

        if (codSolicitacao == null || codSolicitacao.trim().isEmpty()) {
            throw new IllegalArgumentException("O código de solicitação não pode ser nulo ou vazio.");
        }

        if (contaCorrente == null || contaCorrente.trim().isEmpty()) {
            contaCorrente = properties.getNumeroContaCorrente();
        }

        if (contaCorrente == null || contaCorrente.trim().isEmpty()) {
            throw new IllegalArgumentException("A conta corrente não pode ser nula ou vazia.");
        }

        try {
            log.info("Buscando boleto enriquecido para codSolicitacao: {} no ambiente {}", codSolicitacao, ambiente);

            // Obter token OAuth2 com mTLS usando o escopo de leitura de boleto
            String accessToken = interTokenService.obterAccessToken(ambiente, "boleto-cobranca.read");

            // URL completa para o endpoint do boleto
            String boletoUrl = getFullUrl(properties.getBancoInterUrlBoleto() + "/" + codSolicitacao, ambiente);
            log.info("Enviando requisição de busca de boleto para o Banco Inter em: {}", boletoUrl);

            RestTemplate restTemplate = restTemplateUtil.criarRestTemplateMtls();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("x-conta-corrente", contaCorrente);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Boletoenriquecido> response = restTemplate.exchange(
                    boletoUrl,
                    HttpMethod.GET,
                    entity,
                    Boletoenriquecido.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Boleto enriquecido recuperado com sucesso. HTTP Status: {}", response.getStatusCode());
                return response.getBody();
            } else {
                log.error("Falha ao buscar boleto enriquecido no Banco Inter: HTTP status {}", response.getStatusCode());
                throw new RuntimeException("Falha ao buscar boleto enriquecido no Banco Inter: HTTP status " + response.getStatusCode());
            }

        } catch (HttpStatusCodeException e) {
            log.error("Erro HTTP ao buscar boleto enriquecido no Banco Inter. Status: {}, Response: {}", 
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Erro HTTP na API do Banco Inter ao buscar boleto enriquecido: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar boleto enriquecido no Banco Inter: {}", e.getMessage(), e);
            throw new RuntimeException("Erro inesperado ao buscar boleto enriquecido no Banco Inter: " + e.getMessage(), e);
        }
    }

    public BoletoPDFDto obterBoletoPdf(String codSolicitacao, String contaCorrente, String ambiente) {
        if (codSolicitacao == null || codSolicitacao.trim().isEmpty()) {
            throw new IllegalArgumentException("O código de solicitação não pode ser nulo ou vazio.");
        }

        if (ambiente == null || ambiente.trim().isEmpty()) {
            ambiente = "SANDBOX";
        }

        if (contaCorrente == null || contaCorrente.trim().isEmpty()) {
            contaCorrente = properties.getNumeroContaCorrente();
        }

        if (contaCorrente == null || contaCorrente.trim().isEmpty()) {
            throw new IllegalArgumentException("A conta corrente não pode ser nula ou vazia.");
        }

        try {
            log.info("Buscando PDF do boleto para codSolicitacao: {} no ambiente {}", codSolicitacao, ambiente);

            // Obter token OAuth2 com mTLS usando o escopo de leitura de boleto
            String accessToken = interTokenService.obterAccessToken(ambiente, "boleto-cobranca.read");

            // URL completa para o endpoint do PDF
            String pdfUrl = getFullUrl(properties.getBancoInterUrlBoleto() + "/" + codSolicitacao + "/pdf", ambiente);
            log.info("Enviando requisição de PDF do boleto para o Banco Inter em: {}", pdfUrl);

            RestTemplate restTemplate = restTemplateUtil.criarRestTemplateMtls();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("x-conta-corrente", contaCorrente);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<BoletoPDFDto> response = restTemplate.exchange(
                    pdfUrl,
                    HttpMethod.GET,
                    entity,
                    BoletoPDFDto.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("PDF do boleto recuperado com sucesso. HTTP Status: {}", response.getStatusCode());
                return response.getBody();
            } else {
                log.error("Falha ao buscar PDF do boleto no Banco Inter: HTTP status {}", response.getStatusCode());
                throw new RuntimeException("Falha ao buscar PDF do boleto no Banco Inter: HTTP status " + response.getStatusCode());
            }

        } catch (HttpStatusCodeException e) {
            log.error("Erro HTTP ao buscar PDF do boleto no Banco Inter. Status: {}, Response: {}", 
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Erro HTTP na API do Banco Inter ao buscar PDF do boleto: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar PDF do boleto no Banco Inter: {}", e.getMessage(), e);
            throw new RuntimeException("Erro inesperado ao buscar PDF do boleto no Banco Inter: " + e.getMessage(), e);
        }
    }
}