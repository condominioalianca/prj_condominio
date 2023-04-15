package com.condominio.novaalianca.dto.boleto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class PagadorDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String cpfCnpj;
	private String tipoPessoa;
	private String nome;
	private String endereco;
	private String cidade;
	private String uf;
	private String cep;
	private String numero;
	private String complemento;
	private String bairro;
	private String email;
	private String ddd;
	private String telefone;






	

		
}
