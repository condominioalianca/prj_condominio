package com.condominio.novaalianca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadeDTO {

    private Integer idUnidade;

    private String	numeroUnidade;

    private String	andarUnidade;
}
