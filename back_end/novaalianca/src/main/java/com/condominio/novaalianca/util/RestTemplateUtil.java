package com.condominio.novaalianca.util;

import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.config.NovaAliancaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.KeyStore;

/**
 * Utilitário responsável por configurar e fornecer instâncias de RestTemplate 
 * configuradas com mTLS (autenticação mútua SSL) para integrações seguras.
 */
@Component
@RequiredArgsConstructor
public class RestTemplateUtil {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateUtil.class);

    private final NovaAliancaProperties properties;

    private final CaminhoArquivosUtil caminhoArquivosUtil;

    /**
     * Cria e configura um RestTemplate para mTLS (utilizando o certificado PFX).
     */
    public RestTemplate criarRestTemplateMtls() throws Exception {
        String certPath = caminhoArquivosUtil.caminhoCertificado();
        String senha = properties.getSenhaCertificado();

        log.debug("Configurando RestTemplate com mTLS usando o certificado: {}", certPath);

        SSLContext sslContext = buildSslContext(certPath, senha);

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(java.net.HttpURLConnection connection, String httpMethod) throws java.io.IOException {
                if (connection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
                }
                super.prepareConnection(connection, httpMethod);
            }
        };

        requestFactory.setConnectTimeout(15000);
        requestFactory.setReadTimeout(15000);

        return new RestTemplate(requestFactory);
    }

    /**
     * Constrói o SSLContext usando o KeyStore PKCS12 carregado do certificado.
     */
    private SSLContext buildSslContext(String certPath, String senha) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream is = obterInputStreamCertificado(certPath)) {
            keyStore.load(is, senha.toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, senha.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, new java.security.SecureRandom());
        return sslContext;
    }

    /**
     * Carrega de forma robusta o InputStream do certificado (arquivo local, URI ou classpath).
     */
    private InputStream obterInputStreamCertificado(String certPath) throws Exception {
        if (certPath == null || certPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Caminho do certificado não informado.");
        }
        
        if (certPath.startsWith("file:")) {
            try {
                return new URI(certPath).toURL().openStream();
            } catch (Exception e) {
                String cleanPath = certPath.replaceFirst("^file:/+", "");
                File file = new File(cleanPath);
                if (file.exists()) {
                    return new FileInputStream(file);
                }
            }
        }
        
        File file = new File(certPath);
        if (file.exists()) {
            return new FileInputStream(file);
        }
        
        InputStream cpStream = getClass().getClassLoader().getResourceAsStream(certPath);
        if (cpStream != null) {
            return cpStream;
        }
        
        InputStream cpStream2 = getClass().getResourceAsStream(certPath);
        if (cpStream2 != null) {
            return cpStream2;
        }
        
        try {
            return new URL(certPath).openStream();
        } catch (Exception e) {
            return new FileInputStream(certPath);
        }
    }
}