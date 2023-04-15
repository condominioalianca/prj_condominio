package com.condominio.novaalianca.dto.boleto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoletoDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private PagadorDTO pagador;
	private String dataEmissao;
	private String seuNumero;
	private String dataLimite;
	private String dataVencimento;
	private MensagemDTO mensagem;
	private DescontoDTO desconto1;
	private DescontoDTO desconto2;
	private DescontoDTO desconto3;
	private Float valorNominal;
	private Float valorAbatimento;
	private MultaDTO multa;
	private MoraDTO mora;
	private String cnpjCPFBeneficiario;
	private String numDiasAgenda;
	
	
	public BoletoDTO() {
		pagador = new PagadorDTO();
		mensagem = new MensagemDTO();
		desconto1 = new DescontoDTO();
		desconto2 = new DescontoDTO();
		desconto3 = new DescontoDTO();
		multa = new MultaDTO();
		mora = new MoraDTO();
		
	}
	
	
	
}
