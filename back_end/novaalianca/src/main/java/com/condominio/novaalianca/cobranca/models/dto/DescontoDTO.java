package com.condominio.novaalianca.cobranca.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

	@JsonProperty(value = "codigo")
	private String codigoDesconto;

	private int quantidadeDias;
	private Double taxa;
	private Double valor;
	private String data;
	
	
}
