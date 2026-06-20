package com.condominio.novaalianca.dto.boleto;

import com.condominio.novaalianca.cobranca.models.dto.ContentDTO;
import com.condominio.novaalianca.cobranca.models.dto.ResponseCobrancaDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResponseListagemBoletosDTO {

	private int totalPaginas;

	private int totalElementos;

	private int tamanhoPagina;

	private boolean primeiraPagina;

	private boolean ultimaPagina;

	private int numeroDeElementos;

	private List<ResponseCobrancaDTO> cobrancas;

}
