package com.condominio.novaalianca.job.dto.boleto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FiltroListagemBoletoDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String numConta;
	private String nossoNumero;

	private String motivoCancelamento;
	private String dataInicial;
	private String dataFinal;
	private String ordenarPor;
	private String filtrarDataPor;
	private String filtrarPor;
	private String page;
	private String size;
	private List<String> listNossoNumero = new ArrayList<>();

}
