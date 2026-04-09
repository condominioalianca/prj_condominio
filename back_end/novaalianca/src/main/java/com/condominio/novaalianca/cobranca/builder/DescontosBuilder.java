package com.condominio.novaalianca.cobranca.builder;

import com.condominio.novaalianca.cobranca.models.dto.DescontoDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DescontosBuilder {

    public static List<DescontoDTO> listDescontos(){
        return new ArrayList<DescontoDTO>();
    };
}
