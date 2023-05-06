package com.condominio.novaalianca.dto.boleto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseBoletoDTO {
	
	private String seuNumero;
	private String nossoNumero;
	private String codigoBarras;
	private String linhaDigitavel;
	

}
