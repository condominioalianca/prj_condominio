package com.condominio.novaalianca.banking.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusGeral {
    ATIVO(1, "Ativo"),
    INATIVO(2, "Inativo");

    private final Integer id;
    private final String descricao;
}
