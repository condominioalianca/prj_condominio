package com.condominio.novaalianca.enums.inter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Enum representando as possíveis situações de um boleto.
 */
@Getter
public enum SituacaoBoleto {

    RECEBIDO("RECEBIDO"),
    A_RECEBER("A_RECEBER"),
    MARCADO_RECEBIDO("MARCADO_RECEBIDO"),
    ATRASADO("ATRASADO"),
    CANCELADO("CANCELADO"),
    EXPIRADO("EXPIRADO"),
    FALHA_EMISSAO("FALHA_EMISSAO"),
    EM_PROCESSAMENTO("EM_PROCESSAMENTO"),
    PROTESTO("PROTESTO");

    private final String value;

    SituacaoBoleto(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Retorna o enum correspondente ao valor de string recebido.
     * Trata nulos e valores inválidos de forma amigável.
     */
    @JsonCreator
    public static SituacaoBoleto fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (SituacaoBoleto situacao : SituacaoBoleto.values()) {
            if (situacao.getValue().equalsIgnoreCase(value.trim())) {
                return situacao;
            }
        }
        throw new IllegalArgumentException("Situação de boleto inválida: " + value);
    }
}
