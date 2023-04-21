package com.inter.boletos.client.dto.boleto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ResponseBoletoDetalheDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String nomeBeneficiario;
	private String cnpjCpfBeneficiario;
	private String tipoPessoaBeneficiario;

	private String contaCorrente;
	private String nossoNumero;

	private String seuNumero;

	private PagadorDTO pagador;

	private String situacao;

	private String dataHoraSituacao;

	private String dataVencimento;
	private Float valorNominal;

	private String dataEmissao;

	private String dataLimite;

	private String codigoEspecie;

	private String codigoBarras;
	private String linhaDigitavel;
	private String origem;

	private MensagemDTO mensagem;
	private DescontoDTO desconto1;
	private DescontoDTO desconto2;
	private DescontoDTO desconto3;

	private MultaDTO multa;
	private MoraDTO mora;

	private String pdf;
	
	public ResponseBoletoDetalheDTO() {
		mensagem = new MensagemDTO();
		desconto1 = new DescontoDTO();
		desconto2 = new DescontoDTO();
		desconto3 = new DescontoDTO();
		multa = new MultaDTO();
		mora = new MoraDTO();
	}
	
}
