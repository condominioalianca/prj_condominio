package com.condominio.novaalianca.dto.inter.banking;

import com.condominio.novaalianca.enums.inter.TipoOperacaoEnum;
import com.condominio.novaalianca.enums.inter.TipoTransacaoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa os dados simplificados de uma transação do Banco Inter.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoSimplesDTO {

    private String cpmf;
    private String dataEntrada;
    private TipoTransacaoEnum tipoTransacao;
    private TipoOperacaoEnum tipoOperacao;
    private String valor;
    private String titulo;
    private String descricao;
}
