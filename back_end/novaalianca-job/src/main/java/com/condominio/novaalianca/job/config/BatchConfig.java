package com.condominio.novaalianca.job.config;


import com.condominio.novaalianca.job.listener.JobListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;


@Configuration
public class BatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public Job geraBoletoEnviaEmail() {
        return jobBuilderFactory.get("geraBoletoEnviaEmail") //
                .incrementer(new RunIdIncrementer()) //
                .listener(this.listener()) //
                .flow(this.flowStepSearchUsers()) //
                .end() //
                .build();
    }

    @Bean
    public JobExecutionListenerSupport listener() {
        return new JobListener();
    }

}
