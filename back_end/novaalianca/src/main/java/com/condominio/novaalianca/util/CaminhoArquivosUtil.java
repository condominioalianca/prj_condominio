package com.condominio.novaalianca.util;


import com.condominio.novaalianca.config.NovaAliancaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CaminhoArquivosUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaminhoArquivosUtil.class);

    @Autowired
    private NovaAliancaProperties properties;

    public String caminhoCertificado() {
        String caminhoArquivo = properties.getCaminhoCertificado();
        LOGGER.info("Caminho do certificado configurado nas propriedades: {}", caminhoArquivo);

        if (properties.isDocker()) {
            if (caminhoArquivo != null && caminhoArquivo.startsWith("src/main/resources/certs/")) {
                String nomeArquivo = caminhoArquivo.substring("src/main/resources/certs/".length());
                caminhoArquivo = "/etc/certs/" + nomeArquivo;
            }
            LOGGER.info("Caminho do certificado resolvido para o Docker: {}", caminhoArquivo);
        } else {
            String basePath = getClass().getResource("../").toString();
            LOGGER.info("Caminho do class loader: {}", basePath);
            String webDir = "novaalianca/";
            int index = basePath.indexOf(webDir);
            if (index != -1) {
                basePath = basePath.substring(6, index + webDir.length());
                caminhoArquivo = basePath + caminhoArquivo;
            }
            LOGGER.info("Caminho do certificado resolvido localmente: {}", caminhoArquivo);
        }
        return caminhoArquivo;
    }
}
