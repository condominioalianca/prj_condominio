package com.condominio.novaalianca.dto.inter.cobranca;

import com.condominio.novaalianca.dto.inter.cobranca.enums.TipoPessoa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pessoa {
    private String cpfCnpj;
    private String nome;
    private String email;
    private String telefone;
    private String cep;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String ddd;
    private TipoPessoa tipoPessoa;
}
