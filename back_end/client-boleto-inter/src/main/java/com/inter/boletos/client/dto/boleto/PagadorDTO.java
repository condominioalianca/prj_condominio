package com.inter.boletos.client.dto.boleto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class PagadorDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String cpfCnpj;
	private String tipoPessoa;
	private String nome;
	private String endereco;
	private String numero;
	private String complemento;
	private String bairro;
	private String cidade;
	private String uf;
	private String cep;
	private String email;
	private String ddd;
	private String telefone;






	

		
}
