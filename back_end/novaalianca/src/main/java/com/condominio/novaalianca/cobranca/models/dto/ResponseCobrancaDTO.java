package com.condominio.novaalianca.cobranca.models.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ResponseCobrancaDTO {

    private CobrancaDTO cobranca;
    private BoletoDTO boleto;
    private PixDTO pix;
}
