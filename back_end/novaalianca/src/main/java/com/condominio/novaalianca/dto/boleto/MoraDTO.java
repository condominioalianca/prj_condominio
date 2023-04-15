package com.condominio.novaalianca.dto.boleto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MoraDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String codigoMora;
	private String data;
	private Float valor;
	private Float taxa;

	
}
