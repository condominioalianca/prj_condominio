package com.condominio.novaalianca.cobranca.models.dto;

import com.condominio.novaalianca.dto.UnidadeDTO;
import com.condominio.novaalianca.dto.UsuarioDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@Builder
public class BoletoDTO implements Serializable{

	@Serial
	private static final long serialVersionUID = 1L;
	private String nossoNumero;
	private String codigoBarras;
	private String linhaDigitavel;

//	private String seuNumero;
//	private String txCancelamento;
//	private String txSituacao;
//	private LocalDateTime dhSituacao;
//	private LocalDate dtVencimento;
//	private Double valor;
//	private LocalDate dtEmissao;
//	private LocalDate dtLimitePagamento;
//	private String txEspecie;
//	private String txOrigem;
//	private UsuarioDTO usuario;
//	private Double valorPagamento;
//	private String motivoBaixa;
//	private LocalDate dtBaixa;
//	private LocalDate dtPagamento;
//	private LocalDate dtEnvio;
//	private String mesReferencia;
//	private Integer anoReferencia;
//	private UnidadeDTO unidade;
//	private Boolean ativo;



	
}
