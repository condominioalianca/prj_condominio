package com.condominio.novaalianca.util;

//import com.condominio.novaalianca.SchedulesTask.Shedules;
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

        String caminhoArquivo = getClass().getResource("../").toString();

        String webDir = "novaalianca/";
        caminhoArquivo = caminhoArquivo.substring(6, caminhoArquivo.indexOf(webDir)+webDir.length());
        LOGGER.info("Caminho do certificado {}", properties.getCaminhoCertificado());
        caminhoArquivo = caminhoArquivo + properties.getCaminhoCertificado();

        return caminhoArquivo = caminhoArquivo.replace("/", "//");
            }
}
