package com.condominio.novaalianca.job.dto.boleto;

import com.condominio.novaalianca.dto.UnidadeDTO;
import com.condominio.novaalianca.dto.UsuarioDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@Builder
public class BoletoDTO implements Serializable{



	private static final long serialVersionUID = 1L;


	private Long id;

	private String nossoNumero;

	private String seuNumero;

	private String txCancelamento;

	private String txSituacao;

	private LocalDateTime dhSituacao;

	private LocalDate dtVencimento;

	private Double valor;

	private LocalDate dtEmissao;

	private LocalDate dtLimitePagamento;

	private String txEspecie;

	private String txCodBarras;

	private String txLinhaDigitavel;

	private String txOrigem;

	private UsuarioDTO usuario;

	private Double valorPagamento;

	private String motivoBaixa;

	private LocalDate dtBaixa;

	private LocalDate dtPagamento;

	private LocalDate dtEnvio;

	private String mesReferencia;
	private Integer anoReferencia;


	private UnidadeDTO unidade;

	private Boolean ativo;



	
}
