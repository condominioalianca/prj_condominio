package com.condominio.novaalianca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaDTO {
    private Long id;
    private String codBanco;
    private String nrAgencia;
    private String dgAgencia;
    private String nrConta;
    private String dgConta;
    private Long idEmpresa;
}
