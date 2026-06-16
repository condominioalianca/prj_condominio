package com.condominio.novaalianca.enums.inter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Enum representando os tipos de operação no extrato do Banco Inter.
 */
@Getter
public enum TipoOperacaoEnum {

    DEBITO(1, "D", "Débito(Saída)"),
    CREDITO(2, "C", "Crédito(Entrada)");

    private final Integer id;
    private final String value;
    private final String descricao;

    TipoOperacaoEnum(Integer id, String value, String descricao) {
        this.id = id;
        this.value = value;
        this.descricao = descricao;
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
    public static TipoOperacaoEnum fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (TipoOperacaoEnum operacao : TipoOperacaoEnum.values()) {
            if (operacao.getValue().equalsIgnoreCase(value.trim())) {
                return operacao;
            }
        }
        throw new IllegalArgumentException("Tipo de operação inválido: " + value);
    }
}
