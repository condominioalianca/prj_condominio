package com.condominio.novaalianca.job.step;

import com.condominio.novaalianca.job.entities.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.batch.api.chunk.ItemProcessor;

public class GeraBoletoProcessor implements ItemProcessor <Usuario, Usuario> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeraBoletoProcessor.class);

    @Override
    public Usuario processItem(Object item) throws Exception {
        return null;
    }
}
