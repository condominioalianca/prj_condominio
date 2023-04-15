package com.condominio.novaalianca.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseBoletoDTO {
	
	private String seuNumero;
	private String nossoNumero;
	private String codigoBarras;
	private String linhaDigitavel;
	

}
