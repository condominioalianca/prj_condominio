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
public class DescontoDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String codigoDesconto;
	private Double taxa;
	private Double valor;
	private String data;
	
	
}
