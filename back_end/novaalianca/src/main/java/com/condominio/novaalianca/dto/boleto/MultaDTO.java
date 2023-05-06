package com.condominio.novaalianca.dto.boleto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MultaDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String codigoMulta;
	private String data;
	private Float valor;
	private Float taxa;
	

}
