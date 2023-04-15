package com.condominio.novaalianca.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FiltroListagemBoletoDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String numConta;
	private String nossoNumero;
	private String dataInicial;
	private String dataFinal;
	private String ordenarPor;
	private String filtrarDataPor;
	private String filtrarPor;
	private String page;
	private String size;
	private List<String> listNossoNumero = new ArrayList<>();

}
