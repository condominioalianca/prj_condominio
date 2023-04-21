package com.inter.boletos.client.dto.boleto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BeneficiarioDTO {
    private String nome;
    private String cpfCnpj;
    private String tipoPessoa;
    private String cep;
    private String endereco;
    private String bairro;
    private String cidade;
    private String uf;

}
