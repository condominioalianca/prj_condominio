package com.condominio.novaalianca.cobranca.builder;


import com.condominio.novaalianca.cobranca.models.dto.MultaDTO;
import org.springframework.stereotype.Component;

@Component
public class MultaBuilder {

    public static MultaDTO multa (){
        return MultaDTO.builder()
                .codigo("")
//                .taxa()
//                .valor()
                .build();

    }
}
