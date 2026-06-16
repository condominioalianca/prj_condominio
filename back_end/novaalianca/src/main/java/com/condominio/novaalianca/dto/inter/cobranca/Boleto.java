package com.condominio.novaalianca.dto.inter.cobranca;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Boleto {
    private String seuNumero;
    private String dataVencimento;
    private int numDiasAgenda;
    private BigDecimal valorNominal;
    private Mensagem mensagem;
    private Pessoa pagador;
    private Desconto desconto1;
    private Desconto desconto2;
    private Desconto desconto3;
    private Multa multa;
    private Mora mora;
    private Pessoa beneficiarioFinal;
}
