package com.inter.boletos.client.dto.boleto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
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
