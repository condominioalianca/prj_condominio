package com.condominio.novaalianca.dto.boleto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResponseBoletoDetalheDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String nomeBeneficiario;
	private String cnpjCpfBeneficiario;
	private String tipoPessoaBeneficiario;
	private String dataHoraSituacao;
	private String codigoBarras; 
	private String linhaDigitavel;
	private String dataVencimento;
	private String dataEmissao;
	private String descricao;
	private Integer seuNumero;
	private Float valorNominal;
	private String nomePagador;
	private String emailPagador;
	private String dddPagador;
	private String telefonePagador;
	private String tipoPessoaPagador;
	private String cnpjCpfPagador;
	private String codigoEspecie;
	private String dataLimitePagamento;
	private Float valorAbatimento;
	private String situacaoPagamento;
	private Float valorTotalRecebimento;
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
