package com.condominio.novaalianca.cobranca.builder;

import com.condominio.novaalianca.cobranca.models.dto.PixDTO;
import com.condominio.novaalianca.entities.BoletoNovaAlianca;
import org.springframework.stereotype.Component;

@Component
public class PixBuilder {

    private static PixDTO entitytoPixDTO(BoletoNovaAlianca boletoNovaAlianca){
        return PixDTO.builder()
                .txid("")
                .pixCopiaECola("")
                .build();
    }
}
