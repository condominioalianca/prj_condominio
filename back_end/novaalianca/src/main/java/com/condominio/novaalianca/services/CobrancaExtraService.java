package com.condominio.novaalianca.services;

import com.condominio.novaalianca.cobranca.services.BoletoService;
import com.condominio.novaalianca.entities.CobrancaExtra;
import com.condominio.novaalianca.entities.Unidade;
import com.condominio.novaalianca.repositories.CobrancaExtraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CobrancaExtraService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CobrancaExtraService.class);

    @Autowired
    private CobrancaExtraRepository cobrancaExtraRepository;

     public CobrancaExtra getCobrancaExtraByIdUnidadeAndMesReferencia (Unidade unidade, int mesReferencia){
         LOGGER.info("Mes de Referencia Cobranca Extra: {}",mesReferencia);

         return cobrancaExtraRepository.findByidUnidadeAndMesReferencia(unidade,mesReferencia);
     }
}
