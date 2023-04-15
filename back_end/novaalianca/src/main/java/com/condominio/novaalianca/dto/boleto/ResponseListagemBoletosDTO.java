package com.condominio.novaalianca.dto.boleto;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@AllArgsConstructor
public class ResponseListagemBoletosDTO {

	private int totalPages;
	private int totalElements;
	private boolean last;
	private boolean first;
	private int size;
	private int numberOfElements;
	private List<ContentDTO> content;

}
