package com.condominio.novaalianca.services.inter;

import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.config.NovaAliancaProperties;
import com.condominio.novaalianca.util.RestTemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Serviço responsável pela obtenção do token de acesso OAuth2 do Banco Inter,
 * utilizando o RestTemplate mTLS.
 */
@Service
@RequiredArgsConstructor
public class InterTokenService {

    private static final Logger log = LoggerFactory.getLogger(InterTokenService.class);

    private final NovaAliancaProperties properties;

    private final RestTemplateUtil restTemplateUtil;

    private final java.util.Map<String, CachedToken> tokenCache = new java.util.concurrent.ConcurrentHashMap<>();

    private static class CachedToken {
        String token;
        long expiryTimeMillis;
    }

    /**
     * Obtém o token de acesso OAuth2 usando mTLS com cache de 1 hora.
     */
    public synchronized String obterAccessToken(String ambiente, String scope) throws Exception {
        String cacheKey = (ambiente != null ? ambiente.toUpperCase() : "SANDBOX") + ":" + scope;
        CachedToken cached = tokenCache.get(cacheKey);

        // Se o token existe e ainda é válido (com margem de segurança de 30 segundos)
        if (cached != null && System.currentTimeMillis() < cached.expiryTimeMillis - 30000) {
            log.info("Utilizando token de acesso em cache para chave: {}", cacheKey);
            return cached.token;
        }

        String tokenUrl = ambiente != null && !ambiente.equalsIgnoreCase("SANDBOX")
                ? properties.getBancoInterUrlPathProd() + properties.getBancoInterUrlToken()
                : properties.getBancoInterUrlPathSand() + properties.getBancoInterUrlToken();
        
        log.info("Obtendo novo token de acesso do Banco Inter em: {}", tokenUrl);

        RestTemplate restTemplate = restTemplateUtil.criarRestTemplateMtls();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = String.format("client_id=%s&client_secret=%s&grant_type=%s&scope=%s",
                properties.getClientId(),
                properties.getClientSecret(),
                properties.getGrantType() != null ? properties.getGrantType() : "client_credentials",
                scope
        );

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String token = (String) response.getBody().get("access_token");
            Number expiresIn = (Number) response.getBody().get("expires_in");
            long expiresDurationSeconds = expiresIn != null ? expiresIn.longValue() : 3600L;

            CachedToken newCached = new CachedToken();
            newCached.token = token;
            newCached.expiryTimeMillis = System.currentTimeMillis() + (expiresDurationSeconds * 1000L);

            tokenCache.put(cacheKey, newCached);

            log.info("Token de acesso obtido com sucesso e cacheado. Expira em {} segundos.", expiresDurationSeconds);
            return token;
        } else {
            throw new RuntimeException("Falha ao obter token de acesso do Banco Inter: " + response.getStatusCode());
        }
    }
}