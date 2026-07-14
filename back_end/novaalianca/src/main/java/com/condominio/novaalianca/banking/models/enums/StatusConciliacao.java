package com.condominio.novaalianca.banking.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusConciliacao {
    PENDENTE(1, "Pendente"),
    BATIDO(2, "Batido");

    private final Integer id;
    private final String descricao;
}
