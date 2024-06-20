package com.condominio.novaalianca.job.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

public class JobListener extends JobExecutionListenerSupport {

    private static final Logger LOGGER  = LoggerFactory.getLogger(JobListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) { LOGGER.info("SolicitacaoTransferenciaListener afterJob");
    }

    @Override
    public void beforeJob(JobExecution jobExecution) { LOGGER.info("SolicitacaoTransferenciaListener beforeJob");
    }
}
