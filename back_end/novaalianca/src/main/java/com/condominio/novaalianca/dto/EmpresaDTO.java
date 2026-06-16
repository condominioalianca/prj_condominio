package com.condominio.novaalianca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDTO {
    private Long id;
    private String nomeEmpresa;
    private String nrDocumento;
    private String nrCelular;
    private String nrTelefone;
    private String txEndereco;
    private String txEnderecoComplemento;
    private String txEnderecoNumero;
    private String txCep;
    private String txBairro;
    private String txEmail;
}
