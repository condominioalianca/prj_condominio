package com.condominio.novaalianca.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailDTO {

	private String to;
	private String from;
	private String cc;
	private String bcc;
	private String subject;
	private String content;
	private String body;
	private String numeroUnidade;
	private String valorBoleto;
	private String dtVencimento;
	private byte[] anexo;
	
}
