package com.condominio.novaalianca.enums.inter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Enum representando os tipos de transação no extrato do Banco Inter.
 */
@Getter
public enum TipoTransacaoEnum {

    DEBITO_EM_CONTA(1, "DEBITO_EM_CONTA", "Débito em Conta"),
    DEPOSITO_BOLETO(2, "DEPOSITO_BOLETO", "Depósito por Boleto"),
    ANTECIPACAO_RECEBIVEIS(3, "ANTECIPACAO_RECEBIVEIS", "Antecipação de Recebíveis"),
    ANTECIPACAO_RECEBIVEIS_CARTAO(4, "ANTECIPACAO_RECEBIVEIS_CARTAO", "Antecipação de Recebíveis de Cartão"),
    BOLETO_COBRANCA(5, "BOLETO_COBRANCA", "Boleto de Cobrança"),
    CAMBIO(6, "CAMBIO", "Câmbio"),
    CASHBACK(7, "CASHBACK", "Cashback"),
    CHEQUE(8, "CHEQUE", "Cheque"),
    ESTORNO(9, "ESTORNO", "Estorno"),
    DOMICILIO_CARTAO(10, "DOMICILIO_CARTAO", "Domicílio de Cartão"),
    FINANCIAMENTO(11, "FINANCIAMENTO", "Financiamento"),
    IMPOSTO(12, "IMPOSTO", "Imposto"),
    INTERPAG(13, "INTERPAG", "Interpag"),
    INVESTIMENTO(14, "INVESTIMENTO", "Investimento"),
    JUROS(15, "JUROS", "Juros"),
    MAQUININHA_GRANITO(16, "MAQUININHA_GRANITO", "Maquininha Granito"),
    MULTA(17, "MULTA", "Multa"),
    OUTROS(18, "OUTROS", "Outros"),
    PAGAMENTO(19, "PAGAMENTO", "Pagamento"),
    PIX(20, "PIX", "Pix"),
    PROVENTOS(21, "PROVENTOS", "Proventos"),
    SAQUE(22, "SAQUE", "Saque"),
    COMPRA_DEBITO(23, "COMPRA_DEBITO", "Compra no Débito"),
    DEBITO_AUTOMATICO(24, "DEBITO_AUTOMATICO", "Débito Automático"),
    TARIFA(25, "TARIFA", "Tarifa"),
    TRANSFERENCIA(26, "TRANSFERENCIA", "Transferência");

    private final Integer id;
    private final String value;
    private final String descricao;

    TipoTransacaoEnum(Integer id, String value, String descricao) {
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
    public static TipoTransacaoEnum fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (TipoTransacaoEnum tipo : TipoTransacaoEnum.values()) {
            if (tipo.getValue().equalsIgnoreCase(value.trim())) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de transação inválido: " + value);
    }
}
