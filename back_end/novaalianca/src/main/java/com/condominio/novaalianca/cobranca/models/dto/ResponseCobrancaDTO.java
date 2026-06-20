package com.condominio.novaalianca.cobranca.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
