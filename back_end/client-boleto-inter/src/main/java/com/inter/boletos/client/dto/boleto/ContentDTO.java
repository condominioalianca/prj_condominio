package com.inter.boletos.client.dto.boleto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@ToString
public class ContentDTO {

	private String cnpjCpfBeneficiario;
	private String nomeBeneficiario;
	private String contaCorrente;
	private String nossoNumero;
	private String seuNumero;
	private PagadorDTO pagador;
	private String situacao;
	private String dataHoraSituacao;
	private LocalDate dataVencimento;
	private Float valorNominal;
	private Float valorTotalRecebimento;
	private LocalDate dataEmissao;
	private LocalDate dataLimite;
	private String codigoEspecie;
	private String codigoBarras;
	private String linhaDigitavel;
	private String origem;
	private MensagemDTO mensagemDTO;
	private DescontoDTO desconto1;
	private DescontoDTO desconto2;
	private DescontoDTO desconto3;
	private MultaDTO multa;
	private MoraDTO mora;
	
}
