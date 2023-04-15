package com.condominio.novaalianca.dto.responselistagem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SumaryDTO {
	
	private RecebidosDTO recebidos;
	private PrevistosDTO previstos;
	private BaixadosDTO baixados;
	private ExpiradosDTO expirados;

	public SumaryDTO() {
		this.recebidos = new RecebidosDTO();
		this.previstos = new PrevistosDTO();
		this.baixados = new BaixadosDTO();
		this.expirados = new ExpiradosDTO();
	}

	public SumaryDTO(RecebidosDTO recebidos, PrevistosDTO previstos, BaixadosDTO baixados, ExpiradosDTO expirados) {

		this.recebidos = recebidos;
		this.previstos = previstos;
		this.baixados = baixados;
		this.expirados = expirados;
	}
	
	
	
	
	

}
