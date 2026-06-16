package com.condominio.novaalianca.dto.inter.cobranca;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mensagem {
    private String linha1;
    private String linha2;
    private String linha3;
    private String linha4;
    private String linha5;
}
