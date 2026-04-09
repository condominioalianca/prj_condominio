package com.condominio.novaalianca.cobranca.builder;

import com.condominio.novaalianca.cobranca.models.dto.MoraDTO;
import org.springframework.stereotype.Component;

@Component
public class MoraBuilder {

    public static MoraDTO moraDTOBuilder (){
        return MoraDTO.builder()
                .codigo("")
//                .valor()
//                .taxa()
                .build();


    }
}
