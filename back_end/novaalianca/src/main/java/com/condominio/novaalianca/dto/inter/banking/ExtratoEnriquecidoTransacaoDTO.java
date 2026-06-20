package com.condominio.novaalianca.dto.inter.banking;

import com.condominio.novaalianca.enums.inter.TipoOperacaoEnum;
import com.condominio.novaalianca.enums.inter.TipoTransacaoEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa os dados de uma transação individual no Extrato Enriquecido.
 * Utiliza o deserializer customizado para tratar dinamicamente o campo 'detalhes'.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = ExtratoEnriquecidoTransacaoDeserializer.class)
public class ExtratoEnriquecidoTransacaoDTO {

    private String idTransacao;
    private String dataInclusao;
    private String dataTransacao;
    private TipoTransacaoEnum tipoTransacao;
    private TipoOperacaoEnum tipoOperacao;
    private String valor;
    private String titulo;
    private String descricao;
    private String numeroDocumento;
    private Object detalhes;
}
