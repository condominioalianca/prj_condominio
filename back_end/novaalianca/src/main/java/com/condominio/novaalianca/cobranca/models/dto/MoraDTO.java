package com.condominio.novaalianca.cobranca.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoraDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	private String codigo;
	private String data;
	private Float valor;
	private Float taxa;

	
}
