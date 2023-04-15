package com.condominio.novaalianca.dto.boleto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DescontoDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String codigoDesconto;
	private Double taxa;
	private Double valor;
	private String data;
	
	
}
