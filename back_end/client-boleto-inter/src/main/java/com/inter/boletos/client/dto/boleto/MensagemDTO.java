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
public class MensagemDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String linha1;
	private String linha2;
	private String linha3;
	private String linha4;
	private String linha5;
	
		
	
}
